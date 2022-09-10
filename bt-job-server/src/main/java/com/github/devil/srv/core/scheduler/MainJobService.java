package com.github.devil.srv.core.scheduler;

import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.akka.ServerProperties;
import com.github.devil.srv.core.MainThreadUtil;
import com.github.devil.srv.core.notify.NotifyCenter;
import com.github.devil.srv.core.notify.event.ExecuteTooLongTimeEvent;
import com.github.devil.srv.core.notify.event.SchedulerJobErrorEvent;
import com.github.devil.srv.core.notify.listener.ExecuteTooLongTimeListener;
import com.github.devil.srv.core.notify.listener.JobExecuteFailListener;
import com.github.devil.srv.core.notify.listener.SchedulerJobErrorListener;
import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import com.github.devil.srv.core.persist.core.repository.JobInfoRepository;
import com.github.devil.srv.core.persist.core.repository.JobInstanceRepository;
import com.github.devil.srv.core.scheduler.runner.TaskRunner;
import com.github.devil.srv.core.service.JobService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author eric.yao
 * @date 2021/1/28
 **/
@Slf4j
@Component
public class MainJobService implements DisposableBean {

    private final static Integer MAX_BATCH = 500;

    public static final long SCHEDULER_FIX = 800;

    @Resource
    private JobInfoRepository jobInfoRepository;
    @Resource
    private JobInstanceRepository jobInstanceRepository;
    @Resource
    private JobService jobService;
    @Resource
    private TaskRunner taskRunner;
    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * init the schedule task after
     * {@link org.springframework.context.ApplicationContext}
     * is ready and all spring bean has bean init
     */
    public void init(ServerProperties serverProperties){

        /*
         * take the job that has no health server to run
         */
        takeUnServerOrUnHealthServerTask();

        /*
         * process wait task that because the un except stop
         */
        processWaitTask();

        /*
         * register listener to handle the event
         */
        registerListener();

        /*
         * process task from db to timer
         */
        processTaskToTimer();

        /*
         * process task that holder long time to receive the result
         */
        processLongTimeExecutingTask(serverProperties);

        /*
         * register task to release diskspace
         */
        registerClearTask(serverProperties);
    }

    /**
     * process the task that is in WAIT state in app start
     */
    private void processWaitTask(){
        // cancel all wait task before start schedule
        jobService.cancelAllWaitTask(MainAkServer.getCurrentHost());
    }

    /**
     * register listeners
     */
    private void registerListener(){
        /*
         * register the listener
         */
        NotifyCenter.addListener(new JobExecuteFailListener());
        NotifyCenter.addListener(new SchedulerJobErrorListener());
        NotifyCenter.addListener(new ExecuteTooLongTimeListener());
    }

    /**
     * schedule the task to push job to schedule
     * @see #pushJobToTimer
     */
    private void processTaskToTimer(){
        /*
         * register job push task
         */
        MainThreadUtil.JOB_PUSH.scheduleAtFixedRate(() -> {
            try {
                if (MainAkServer.isNormal()) {
                    this.pushJobToTimer();
                }
            }catch (Exception e){
                log.error("job push error,thread-name:{},error",Thread.currentThread().getName(),e);
            }
        },100,SCHEDULER_FIX, TimeUnit.MILLISECONDS);
    }

    /**
     * notify the job that has long time to receive the execute res
     */
    private void processLongTimeExecutingTask(ServerProperties serverProperties){

        int maxSeconds = Optional.ofNullable(serverProperties.getMaxExecuteWaitSeconds()).orElse(0);

        if (maxSeconds > 0) {
            MainThreadUtil.scheduleAtFixedRate(() -> {
                List<InstanceEntity> entities = jobInstanceRepository.findByExecuteStatueAndUptBefore(ExecuteStatue.EXECUTING,
                        new Date(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(maxSeconds)));
                if (entities != null && !entities.isEmpty()) {
                    List<Long> ids = entities.stream().map(InstanceEntity::getId).collect(Collectors.toList());
                    NotifyCenter.onEvent(ExecuteTooLongTimeEvent.builder().instanceIds(ids).build());
                }
            }, 10, Math.max(maxSeconds,60), TimeUnit.SECONDS);
        }
    }

    /**
     * register task to release db and disk if necessary
     * @param serverProperties
     */
    private void registerClearTask(ServerProperties serverProperties){
        int maxInstance = Optional.ofNullable(serverProperties.getMaxTaskInstance()).orElse(0);
        if (maxInstance > 0){
            MainThreadUtil.scheduleAtFixedRate(() -> {
                jobService.clearTask(maxInstance);
            },10,Math.max(maxInstance,60),TimeUnit.SECONDS);
        }
    }

    /**
     * take job that not have the server to schedule
     */
    private void takeUnServerOrUnHealthServerTask(){
        jobService.takeNoServerTask();
    }

    /**
     * push current server task to schedule
     */
    private void pushJobToTimer(){

        /**
         * 当前时间----> 下次定时时间触发的时候，需要执行的任务
         */
        List<JobInfoEntity> jobInfoEntities = jobInfoRepository.findUnExecuteJob(MainAkServer.getCurrentHost(), new Date(System.currentTimeMillis()+(int)(SCHEDULER_FIX*1.5)),Lists.newArrayList(ExecuteStatue.WAIT.name(),ExecuteStatue.EXECUTING.name()));

        for (List<JobInfoEntity> lists : Lists.partition(jobInfoEntities, MAX_BATCH)) {

            transactionTemplate.executeWithoutResult(status -> {
                if (log.isDebugEnabled()) {
                    log.debug("process task to timer,task count :{}", lists.size());
                }

                Map<Long,JobInfoEntity> map = lists.stream().collect(Collectors.toMap(JobInfoEntity::getId,e -> e));

                List<InstanceEntity> instanceEntities = jobService.newInstance(lists);

                instanceEntities.forEach(instanceEntity -> {

                    try {
                        MainJobScheduler.schedule(instanceEntity.getId(),Math.max(instanceEntity.getExceptTriggerTime().getTime() - System.currentTimeMillis(),0),() -> {
                            taskRunner.runTask(map.get(instanceEntity.getJobId()),instanceEntity.getId());
                        });
                    }catch (Exception e){
                        log.error("push job to timer-wheel fail,",e);
                        jobService.failInstance(instanceEntity.getId());
                        NotifyCenter.onEvent(SchedulerJobErrorEvent.builder()
                                            .instanceId(instanceEntity.getId())
                                            .jobId(instanceEntity.getJobId())
                                            .describe(NestedExceptionUtils.buildMessage(null,e))
                                            .build());
                    }
                });
            });
        }
    }

    /**
     * destroy method stop used threadPool
     * and cancel all the task
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        try{
            MainThreadUtil.shutdown();
            MainJobScheduler.stop();
        }catch (Exception e){
            log.error("stop scheduler error,",e);
        }
    }
}
