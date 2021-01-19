package com.github.devil.core.service;

import com.github.devil.core.ha.AppService;
import com.github.devil.core.persist.entity.InstanceEntity;
import com.github.devil.core.persist.entity.JobInfoEntity;
import com.github.devil.core.persist.repository.JobInfoRepository;
import com.github.devil.core.persist.repository.JobInstanceRepository;
import com.github.devil.enums.ExecuteStatue;
import com.github.devil.enums.TimeType;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author eric.yao
 * @date 2021/1/19
 **/
@Component
public class JobService {

    @Resource
    private AppService appSelect;
    @Resource
    private JobInstanceRepository jobInstanceRepository;
    @Resource
    private JobInfoRepository jobInfoRepository;

    @Transactional(transactionManager = "transactionManager")
    public InstanceEntity newInstance(JobInfoEntity jobInfoEntity){
        InstanceEntity instanceEntity = new InstanceEntity();
        BeanUtils.copyProperties(jobInfoEntity,instanceEntity);
        instanceEntity.setExecuteStatue(ExecuteStatue.WAIT);
        instanceEntity.setJobId(jobInfoEntity.getId());
        instanceEntity.setExceptTriggerTime(jobInfoEntity.getNextTriggerTime());
        instanceEntity.setWorkerHost(appSelect.chooseWorker().getAppHost());
        return jobInstanceRepository.saveAndFlush(instanceEntity);
    }

    @Transactional(transactionManager = "transactionManager")
    public void refreshNextTriggerTime(JobInfoEntity jobInfoEntity){
        TimeType timeType = jobInfoEntity.getTimeType();
        Date next = timeType.getNext(jobInfoEntity.getLastTriggerTime(),jobInfoEntity.getTimeVal());
        jobInfoRepository.updateNextAndPreTriggerTimeById(jobInfoEntity.getId(),next,jobInfoEntity.getLastTriggerTime());
    }
}
