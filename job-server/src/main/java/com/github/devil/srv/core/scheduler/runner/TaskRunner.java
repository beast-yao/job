package com.github.devil.srv.core.scheduler.runner;

import akka.actor.ActorSelection;
import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.akka.request.WorkerExecuteReq;
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

    public void runTask(JobInfoEntity jobInfoEntity,Long instanceId){
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

        // 设置开始时间
        instanceEntity.setTriggerTime(new Date());

        WorkInstanceEntity query = new WorkInstanceEntity();
        query.setInstanceId(instanceId);
        List<WorkInstanceEntity> workers = workInstanceRepository.findAll(Example.of(query));
        if (workers.isEmpty()){
            //todo handle empty work
        }else {
            workers.forEach(e -> {
                WorkerExecuteReq req = new WorkerExecuteReq();
                req.setInstanceId(instanceId);
                req.setJobId(jobInfoEntity.getId());
                req.setJobMeta(jobInfoEntity.getJobMeta());
                req.setUniqueName(jobInfoEntity.getUniqueName());
                req.setParams(e.getParams());
                req.setWorkInstanceId(e.getId());

                ActorSelection selection = MainAkServer.getWorker(e.getWorkerHost());
                selection.tell(req,null);

                //todo update workinstance
            });
        }

        instanceEntity.setExecuteStatue(ExecuteStatue.EXECUTING);
        jobInstanceRepository.updateTriggerTimeAndStatus(instanceEntity.getTriggerTime(),instanceEntity.getExecuteStatue(),instanceId);
    }

}
