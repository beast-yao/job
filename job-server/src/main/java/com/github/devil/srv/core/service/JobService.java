package com.github.devil.srv.core.service;

import com.github.devil.common.CommonConstants;
import com.github.devil.common.enums.TaskType;
import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.akka.ha.ServerManager;
import com.github.devil.srv.akka.worker.WorkerHolder;
import com.github.devil.srv.core.exception.JobException;
import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import com.github.devil.srv.core.persist.core.entity.WorkInstanceEntity;
import com.github.devil.srv.core.persist.core.repository.JobInfoRepository;
import com.github.devil.srv.core.persist.core.repository.JobInstanceRepository;
import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.common.enums.TimeType;
import com.github.devil.srv.core.persist.core.repository.WorkInstanceRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author eric.yao
 * @date 2021/1/19
 **/
@Component
public class JobService {

    @Resource
    private JobInstanceRepository jobInstanceRepository;
    @Resource
    private JobInfoRepository jobInfoRepository;
    @Resource
    private WorkInstanceRepository workInstanceRepository;
    @Resource
    private ServerManager serverManager;

    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    public InstanceEntity newInstance(JobInfoEntity jobInfoEntity){
        InstanceEntity instanceEntity = new InstanceEntity();
        BeanUtils.copyProperties(jobInfoEntity,instanceEntity);
        instanceEntity.setExecuteStatue(ExecuteStatue.WAIT);
        instanceEntity.setJobId(jobInfoEntity.getId());
        instanceEntity.setExceptTriggerTime(jobInfoEntity.getNextTriggerTime());
        instanceEntity.setServeHost(MainAkServer.getCurrentHost());
        jobInstanceRepository.saveAndFlush(instanceEntity);

        List<WorkInstanceEntity> lists = getAllWorkers(jobInfoEntity).stream().map(s -> {
            WorkInstanceEntity workInstanceEntity = new WorkInstanceEntity();
            BeanUtils.copyProperties(instanceEntity,workInstanceEntity);
            workInstanceEntity.setInstanceId(instanceEntity.getId());
            workInstanceEntity.setWorkerHost(s);
            return workInstanceEntity;
        }).collect(Collectors.toList());

        if (!lists.isEmpty()) {
            workInstanceRepository.saveAll(lists);
        }
        return instanceEntity;
    }

    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    public List<InstanceEntity> newInstance(List<JobInfoEntity> jobInfoEntities){

        List<InstanceEntity> instanceEntities = jobInfoEntities.stream().map(jobInfoEntity -> {
            InstanceEntity instanceEntity = new InstanceEntity();
            BeanUtils.copyProperties(jobInfoEntity,instanceEntity);
            instanceEntity.setExecuteStatue(ExecuteStatue.WAIT);
            instanceEntity.setJobId(jobInfoEntity.getId());
            instanceEntity.setExceptTriggerTime(jobInfoEntity.getNextTriggerTime());
            instanceEntity.setServeHost(MainAkServer.getCurrentHost());
            return instanceEntity;
        }).collect(Collectors.toList());

        List<WorkInstanceEntity> lists = jobInfoEntities.stream().flatMap(e -> this.getAllWorkers(e).stream()).map(s ->
            instanceEntities.stream().map(instanceEntity -> {
                        WorkInstanceEntity workInstanceEntity = new WorkInstanceEntity();
                        BeanUtils.copyProperties(instanceEntity, workInstanceEntity);
                        workInstanceEntity.setInstanceId(instanceEntity.getId());
                        workInstanceEntity.setWorkerHost(s);
                        return workInstanceEntity;
                    }).collect(Collectors.toList())

            ).flatMap(List::stream).collect(Collectors.toList());

        jobInstanceRepository.saveAll(instanceEntities);

        if (!lists.isEmpty()) {
            workInstanceRepository.saveAll(lists);
        }
        return instanceEntities;
    }

    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    public void refreshNextTriggerTime(JobInfoEntity jobInfoEntity){
        TimeType timeType = jobInfoEntity.getTimeType();
        Date next = timeType.getNext(jobInfoEntity.getLastTriggerTime(),jobInfoEntity.getTimeVal());
        jobInfoRepository.updateNextAndPreTriggerTimeAndServerById(jobInfoEntity.getId(),next,jobInfoEntity.getLastTriggerTime(),MainAkServer.nextHealthServer());
    }

    private Set<String> getAllWorkers(JobInfoEntity jobInfoEntity){
        //未指定机器执行任务
        if (StringUtils.isEmpty(jobInfoEntity.getWorkerHost())){
            switch (jobInfoEntity.getExecuteType()){
                case SINGLE:
                    return Sets.newHashSet(serverManager.getNextWorker(jobInfoEntity.getAppName()));
                case BROADCAST:
                    return WorkerHolder.getSurvivalWorkers(jobInfoEntity.getAppName());
                default:
                    throw new JobException("un support execute type");
            }
        }else {
           return Sets.newHashSet(jobInfoEntity.getWorkerHost().split(CommonConstants.COMMON_SPLIT));
        }
    }
}
