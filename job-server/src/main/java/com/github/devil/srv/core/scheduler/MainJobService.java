package com.github.devil.srv.core.scheduler;

import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.core.MainThreadUtil;
import com.github.devil.srv.core.notify.NotifyCenter;
import com.github.devil.srv.core.notify.listener.JobExecuteFailListener;
import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import com.github.devil.srv.core.persist.core.repository.JobInfoRepository;
import com.github.devil.srv.core.scheduler.runner.TaskRunner;
import com.github.devil.srv.core.service.JobService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

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

    public void init(){
        /**
         * process wait task that because the un except stop
         */
        processWaitTask();

        /**
         * register the listener
         */
        NotifyCenter.addListener(new JobExecuteFailListener());

        /**
         * process task from db to timer
         */
        processTaskToTimer();

        /**
         * process task that holder long time to receive the result
         */
        processLongTimeExecutingTask();
    }

    public void processWaitTask(){
        //todo
    }

    public void processTaskToTimer(){
        /**
         * register job push task
         */
        MainThreadUtil.JOB_PUSH.scheduleAtFixedRate(() -> {
            try {
                this.pushJobToTimer();
            }catch (Exception e){
                log.error("job push error,thread-name:{},error",Thread.currentThread().getName(),e);
            }
        },100,SCHEDULER_FIX, TimeUnit.MILLISECONDS);
    }

    public void processLongTimeExecutingTask(){
        //todo
    }

    private void pushJobToTimer(){

        /**
         * 当前时间----> 下次定时时间触发的时候，需要执行的任务
         */
        List<JobInfoEntity> jobInfoEntities = jobInfoRepository.findUnExecuteJob(MainAkServer.getCurrentHost(), new Date(System.currentTimeMillis()+(int)(SCHEDULER_FIX*1.5)),Lists.newArrayList(ExecuteStatue.WAIT.name(),ExecuteStatue.EXECUTING.name()));

        for (List<JobInfoEntity> lists : Lists.partition(jobInfoEntities, MAX_BATCH)) {

            try {
                if (log.isDebugEnabled()) {
                    log.debug("process task to timer,task count :{}", lists.size());
                }

                Map<Long,JobInfoEntity> map = lists.stream().collect(Collectors.toMap(JobInfoEntity::getId,e -> e));

                List<InstanceEntity> instanceEntities = jobService.newInstance(lists);

                instanceEntities.forEach(instanceEntity -> {

                    MainJobScheduler.schedule(instanceEntity.getId(),Math.max(instanceEntity.getExceptTriggerTime().getTime() - System.currentTimeMillis(),0),() -> {
                        taskRunner.runTask(map.get(instanceEntity.getJobId()),instanceEntity.getId());
                    });
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        try{
            MainJobScheduler.stop();
        }catch (Exception e){
            log.error("stop scheduler error,",e);
        }
    }
}
