package com.github.devil.srv.core.scheduler;

import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.core.MainThreadUtil;
import com.github.devil.srv.core.notify.NotifyCenter;
import com.github.devil.srv.core.notify.event.SchedulerJobErrorEvent;
import com.github.devil.srv.core.notify.listener.JobExecuteFailListener;
import com.github.devil.srv.core.notify.listener.SchedulerJobErrorListener;
import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import com.github.devil.srv.core.persist.core.repository.JobInfoRepository;
import com.github.devil.srv.core.scheduler.runner.TaskRunner;
import com.github.devil.srv.core.service.JobService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author eric.yao
 * @date 2021/1/28
 **/
@Slf4j
@Component
public class MainJobService implements DisposableBean {

    private final static Integer MAX_BATCH = 50;

    public static final long SCHEDULER_FIX = 800;

    @Resource
    private JobInfoRepository jobInfoRepository;
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
    public void init(){
        /**
         * process wait task that because the un except stop
         */
        processWaitTask();

        /**
         * take the job that has no server to run
         */
        takeUnServerTask();

        /**
         * register listener to handle the event
         */
        registerListener();

        /**
         * process task from db to timer
         */
        processTaskToTimer();

        /**
         * process task that holder long time to receive the result
         */
        processLongTimeExecutingTask();
    }

    /**
     * process the task that is in WAIT state in app start
     */
    private void processWaitTask(){
        //todo
        MainThreadUtil.scheduleAtFixedRate(() -> {

        },10,SCHEDULER_FIX,TimeUnit.MILLISECONDS);
    }

    /**
     * register listeners
     */
    private void registerListener(){
        /**
         * register the listener
         */
        NotifyCenter.addListener(new JobExecuteFailListener());
        NotifyCenter.addListener(new SchedulerJobErrorListener());
    }

    /**
     * schedule the task to push job to schedule
     * @see #pushJobToTimer
     */
    private void processTaskToTimer(){
        /**
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
    private void processLongTimeExecutingTask(){
        //todo
    }

    /**
     * take job that not have the server to schedule
     */
    private void takeUnServerTask(){
        //todo
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
                    log.error("timer delay the job fail,",e);
                    jobService.failInstance(instanceEntity.getId());
                    NotifyCenter.onEvent(SchedulerJobErrorEvent.builder()
                                        .instanceId(instanceEntity.getId())
                                        .jobId(instanceEntity.getJobId())
                                        .describe(e.getMessage())
                                        .build());
                }
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
