package com.github.devil.srv.core.scheduler.runner;

import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import com.github.devil.srv.core.persist.core.repository.JobInstanceRepository;
import com.github.devil.srv.core.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
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

        instanceEntity.setExecuteStartTime(new Date());


    }

}
