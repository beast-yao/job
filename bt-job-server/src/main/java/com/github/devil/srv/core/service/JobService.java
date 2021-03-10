package com.github.devil.srv.core.service;

import com.github.devil.common.CommonConstants;
import com.github.devil.common.enums.ResultEnums;
import com.github.devil.common.dto.WorkerExecuteRes;
import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.akka.ha.ServerManager;
import com.github.devil.srv.akka.worker.WorkerHolder;
import com.github.devil.srv.core.enums.InstanceType;
import com.github.devil.srv.core.exception.JobException;
import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import com.github.devil.srv.core.persist.core.entity.WorkInstanceEntity;
import com.github.devil.srv.core.persist.core.repository.JobInfoRepository;
import com.github.devil.srv.core.persist.core.repository.JobInstanceRepository;
import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.common.enums.TimeType;
import com.github.devil.srv.core.persist.core.repository.WorkInstanceRepository;
import com.github.devil.srv.core.persist.logging.repository.LoggingRepository;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author eric.yao
 * @date 2021/1/19
 **/
@Slf4j
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
    @Resource
    private LoggingRepository loggingRepository;

    private final static Integer HAND_VERSION = -1;

    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    public InstanceEntity newHandInstance(JobInfoEntity jobInfoEntity){
        InstanceEntity instanceEntity = new InstanceEntity();
        BeanUtils.copyProperties(jobInfoEntity,instanceEntity);
        instanceEntity.setExecuteStatue(ExecuteStatue.WAIT);
        instanceEntity.setJobId(jobInfoEntity.getId());
        instanceEntity.setExceptTriggerTime(jobInfoEntity.getNextTriggerTime());
        instanceEntity.setServeHost(MainAkServer.getCurrentHost());
        instanceEntity.setInstanceType(InstanceType.HAND);
        // 手动执行版本号为-1
        instanceEntity.setVersion(HAND_VERSION);
        instanceEntity.setId(null);
        jobInstanceRepository.saveAndFlush(instanceEntity);

        List<WorkInstanceEntity> lists = getAllWorkers(jobInfoEntity).stream().map(s -> {
            WorkInstanceEntity workInstanceEntity = new WorkInstanceEntity();
            BeanUtils.copyProperties(instanceEntity,workInstanceEntity);
            workInstanceEntity.setInstanceId(instanceEntity.getId());
            workInstanceEntity.setWorkerHost(s);
            workInstanceEntity.setId(null);
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
            instanceEntity.setInstanceType(InstanceType.AUTO);
            instanceEntity.setId(null);
            return instanceEntity;
        }).collect(Collectors.toList());

        jobInstanceRepository.saveAll(instanceEntities);

        List<WorkInstanceEntity> lists = jobInfoEntities.stream().flatMap(e -> this.getAllWorkers(e).stream()).filter(Objects::nonNull).map(s ->
            instanceEntities.stream().map(instanceEntity -> {
                        WorkInstanceEntity workInstanceEntity = new WorkInstanceEntity();
                        BeanUtils.copyProperties(instanceEntity, workInstanceEntity);
                        workInstanceEntity.setInstanceId(instanceEntity.getId());
                        workInstanceEntity.setWorkerHost(s);
                        workInstanceEntity.setId(null);
                        return workInstanceEntity;
                    }).collect(Collectors.toList())

            ).flatMap(List::stream).collect(Collectors.toList());

        if (!lists.isEmpty()) {
            workInstanceRepository.saveAll(lists);
        }
        return instanceEntities;
    }

    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    public void refreshNextTriggerTime(InstanceEntity instanceEntity){
        TimeType timeType = instanceEntity.getTimeType();
        Date next = timeType.getNext(Objects.equals(instanceEntity.getTimeType(),TimeType.DELAY)?instanceEntity.getExecuteEndTime():instanceEntity.getTriggerTime(),instanceEntity.getTimeVal());
        jobInfoRepository.updateNextAndPreTriggerTimeAndServerById(instanceEntity.getJobId(),next,instanceEntity.getTriggerTime(),MainAkServer.nextHealthServer(),MainAkServer.getCurrentHost());
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

    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    public void handleWorkRes(WorkerExecuteRes res){

        /**
         * 修改任务执行状态
         */
        workInstanceRepository.endWorkFromExecuteById(res.getWorkInstanceId(),res.getResult().equals(ResultEnums.S)?ExecuteStatue.SUCCESS:ExecuteStatue.FAILURE,new Date());

        List<WorkInstanceEntity> lists = workInstanceRepository.findByExecuteStatueInAndInstanceId(Arrays.asList(ExecuteStatue.EXECUTING,ExecuteStatue.WAIT),res.getInstanceId());
        /**
         * 所有子任务执行结束，需要更新task状态
         */
        if (lists.isEmpty()){
            List<WorkInstanceEntity> fails = workInstanceRepository.findByExecuteStatueInAndInstanceId(Collections.singletonList(ExecuteStatue.FAILURE),res.getInstanceId());
            jobInstanceRepository.updateStatusById(res.getInstanceId(),new Date(),fails.isEmpty()?ExecuteStatue.SUCCESS:ExecuteStatue.FAILURE);
            Optional<InstanceEntity> optional = jobInstanceRepository.findById(res.getInstanceId());
            optional.ifPresent(entity -> {
                if (Objects.equals(entity.getTimeType(),TimeType.DELAY)){
                    refreshNextTriggerTime(entity);
                }
            });
        }
    }

    /**
     * 直接失败任务
     * @param instanceId
     */
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    public void failInstance(Long instanceId){
        jobInstanceRepository.updateStatusById(instanceId,new Date(),ExecuteStatue.FAILURE);

        workInstanceRepository.endWorkByInstanceId(ExecuteStatue.FAILURE,new Date(),new Date(),instanceId,ExecuteStatue.WAIT);
    }

    /**
     * 取消所有的待执行任务
     * @param serverHost
     */
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    public void cancelAllWaitTask(String serverHost){
        List<InstanceEntity> entities = jobInstanceRepository.findByServeHostAndExecuteStatueIn(serverHost, Collections.singletonList(ExecuteStatue.WAIT));

        if (!entities.isEmpty()) {
            jobInstanceRepository.cancelAllWaitTask(serverHost,new Date());
            workInstanceRepository.cancelAllInstance(serverHost,new Date());

            log.info("cancel task from server:[{}],task count:[{}]",serverHost,entities.size());
        }
    }

    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    public void takeNoServerTask(){
        int i = jobInfoRepository.updateServerHostWhereNull(MainAkServer.getCurrentHost(),new Date());
        if ( i > 0 ) {
            log.warn("process task that has no server hold,task count:[{}],that may impossible",i);
        }
    }

    public void clearTask(int maxInstances){

        Pageable pageable = PageRequest.of(0,maxInstances, Sort.by(Sort.Direction.DESC,"id"));
        Page<InstanceEntity> instances = jobInstanceRepository.findAll(pageable);

        if (instances.getTotalElements() > maxInstances){
            long minInstanceId = instances.get().mapToLong(InstanceEntity::getId).min().orElse(0);
            jobInstanceRepository.deleteByIdLessThan(minInstanceId);
            workInstanceRepository.deleteByInstanceIdLessThan(minInstanceId);
            loggingRepository.deleteByInstanceIdLessThan(minInstanceId);
        }
    }
}
