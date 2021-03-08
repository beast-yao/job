package com.github.devil.srv.core.scheduler.runner;

import akka.actor.ActorSelection;
import akka.pattern.Patterns;
import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.common.dto.WorkerExecuteReq;
import com.github.devil.common.enums.TimeType;
import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.core.enums.InstanceType;
import com.github.devil.srv.core.exception.JobException;
import com.github.devil.srv.core.notify.NotifyCenter;
import com.github.devil.srv.core.notify.event.JobExecuteFailEvent;
import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import com.github.devil.srv.core.persist.core.entity.WorkInstanceEntity;
import com.github.devil.srv.core.persist.core.repository.JobInstanceRepository;
import com.github.devil.srv.core.persist.core.repository.WorkInstanceRepository;
import com.github.devil.srv.core.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import scala.concurrent.Future;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * @author eric.yao
 * @date 2021/1/25
 **/
@Slf4j
@Component
public class TaskRunner {

    @Resource
    private JobInstanceRepository jobInstanceRepository;
    @Resource
    private WorkInstanceRepository workInstanceRepository;
    @Resource
    private JobService jobService;

    public void runTask(JobInfoEntity jobInfoEntity,Long instanceId){
        jobInfoEntity.setLastTriggerTime(new Date());
        Optional<InstanceEntity> optional = jobInstanceRepository.findById(instanceId);
        if (!optional.isPresent()){
            log.error("args error cannot find instance info by id :instanceId:{},jobId:{}",instanceId,jobInfoEntity.getId());
            return;
        }
        InstanceEntity instanceEntity = optional.get();

        if (Objects.equals(instanceEntity.getExecuteStatue(), ExecuteStatue.CANCEL)){
            log.warn("current execute has being canceled,instanceId:{}",instanceId);
            return;
        }

        // 不是延时任务和手动执行任务，需要修改触发时间
        // 此时就可以进行下一次任务调度了
        if (!Objects.equals(jobInfoEntity.getTimeType(), TimeType.DELAY)
                && !Objects.equals(instanceEntity.getInstanceType(), InstanceType.HAND)) {
            jobService.refreshNextTriggerTime(jobInfoEntity);
        }
        // 设置开始时间
        instanceEntity.setTriggerTime(jobInfoEntity.getLastTriggerTime());

        // 设置为执行状态
        jobInstanceRepository.updateTriggerTimeAndStatus(instanceEntity.getTriggerTime(),ExecuteStatue.EXECUTING,instanceId);

        WorkInstanceEntity query = new WorkInstanceEntity();
        query.setInstanceId(instanceId);
        List<WorkInstanceEntity> workers = workInstanceRepository.findAll(Example.of(query));
        if (workers.isEmpty()){

            log.error("can not find any worker to submit this task,instanceId:{}",instanceId);

            /**
             * 直接失败任务
             */
            jobService.failInstance(instanceId);

            NotifyCenter.onEvent(new JobExecuteFailEvent()
                    .setInstanceId(instanceId)
                    .setJobId(jobInfoEntity.getId())
                    .setAppName(instanceEntity.getAppName())
                    .setException(new JobException("can not find any worker to submit this task,Please check if all worker is down")));

        }else {
            for (WorkInstanceEntity worker : workers) {
                WorkerExecuteReq req = new WorkerExecuteReq();
                req.setInstanceId(instanceId);
                req.setJobId(jobInfoEntity.getId());
                req.setJobMeta(jobInfoEntity.getJobMeta());
                req.setUniqueName(jobInfoEntity.getUniqueName());
                req.setParams(worker.getParams());
                req.setWorkInstanceId(worker.getId());
                req.setTaskType(instanceEntity.getTaskType());
                req.setServerHost(MainAkServer.getCurrentHost());


                ActorSelection selection = MainAkServer.getWorker(worker.getWorkerHost());
                // 不需要返回结果，只需要确保接收到了消息
                try {
                    CompletionStage<?> stage = Patterns.ask(selection,req, Duration.ofMillis(1000));
                    stage.toCompletableFuture().get(1000, TimeUnit.MILLISECONDS);

                    workInstanceRepository.updateTriggerTimeAndExecuteStatueById(ExecuteStatue.EXECUTING,new Date(),new Date(),worker.getId());

                }catch (Exception e){
                    log.error("send execute req error,",e);
                    workInstanceRepository.endWorkById(ExecuteStatue.FAILURE,new Date(),worker.getId(),ExecuteStatue.WAIT);
                    worker.setExecuteStatue(ExecuteStatue.FAILURE);

                    NotifyCenter.onEvent(new JobExecuteFailEvent()
                            .setInstanceId(instanceId)
                            .setJobId(jobInfoEntity.getId())
                            .setAppName(instanceEntity.getAppName())
                            .setWorkInstanceId(worker.getId())
                            .setWorkHost(worker.getWorkerHost())
                            .setException(new JobException("send execute req error",e)));

                }
            }
            // whether all the work task is fail
            boolean hasNoFail = workers.stream().anyMatch(e -> !Objects.equals(e.getExecuteStatue(), ExecuteStatue.FAILURE));
            if (!hasNoFail) {
                jobInstanceRepository.updateStatusById(instanceId,new Date(),ExecuteStatue.FAILURE);
            }
        }
    }

}
