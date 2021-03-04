package com.github.devil.srv.service.impl;

import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.core.enums.JobStatus;
import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import com.github.devil.srv.core.persist.core.repository.JobInfoRepository;
import com.github.devil.srv.core.persist.core.repository.JobInstanceRepository;
import com.github.devil.srv.core.persist.core.repository.WorkInstanceRepository;
import com.github.devil.srv.dto.request.NewTaskRequest;
import com.github.devil.srv.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author eric.yao
 * @date 2021/2/18
 **/
@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private JobInfoRepository jobInfoRepository;
    @Resource
    private JobInstanceRepository instanceRepository;
    @Resource
    private WorkInstanceRepository workInstanceRepository;

    @Override
    public Boolean newTask(NewTaskRequest request) {

        JobInfoEntity entity = new JobInfoEntity();
        entity.setLastTriggerTime(null);
        entity.setJobMeta(null);
        entity.setAppName(request.getAppName());
        entity.setExecuteType(request.getExecuteType());
        entity.setJobStatus(JobStatus.NORMAL);
        entity.setNextTriggerTime(request.getTimeType().getNext(new Date(),request.getTimeExpression()));
        entity.setTaskType(request.getTaskType());
        entity.setVersion(0);
        entity.setWorkerHost(request.getWorkHost());
        entity.setTimeVal(request.getTimeExpression());
        entity.setUniqueName(request.getTaskName());
        entity.setTimeType(request.getTimeType());
        entity.setServeHost(MainAkServer.getCurrentHost());
        jobInfoRepository.saveAndFlush(entity);
        return true;
    }

    @Override
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    public void stopAllAndTransfer(String serverHost) {

        if(jobInfoRepository.countByServeHost(serverHost) > 0) {

            List<InstanceEntity> entities = instanceRepository.findByServeHostAndExecuteStatueIn(serverHost, Collections.singletonList(ExecuteStatue.WAIT));

            if (!entities.isEmpty()) {
                instanceRepository.cancelAllWaitTask(serverHost);
                workInstanceRepository.cancelAllInstance(serverHost);
            }

            jobInfoRepository.transferToAnotherServer(serverHost, MainAkServer.nextHealthServer());
        }
    }

}
