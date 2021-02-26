package com.github.devil.srv.core.scheduler.runner;

import akka.actor.ActorSelection;
import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.common.request.WorkerExecuteReq;
import com.github.devil.srv.akka.MainAkServer;
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

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

        jobService.refreshNextTriggerTime(jobInfoEntity);
        // 设置开始时间
        instanceEntity.setTriggerTime(jobInfoEntity.getLastTriggerTime());

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
                selection.tell(req,MainAkServer.getActorRef());

                workInstanceRepository.mergeTriggerTimeAndExecuteStatueById(ExecuteStatue.EXECUTING,new Date(),new Date(),worker.getId());
            }
        }

        instanceEntity.setExecuteStatue(ExecuteStatue.EXECUTING);
        jobInstanceRepository.updateTriggerTimeAndStatus(instanceEntity.getTriggerTime(),instanceEntity.getExecuteStatue(),instanceId,new Date());
    }

}
