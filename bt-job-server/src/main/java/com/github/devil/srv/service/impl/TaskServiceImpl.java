package com.github.devil.srv.service.impl;

import com.github.devil.common.enums.TaskType;
import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.core.enums.JobStatus;
import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import com.github.devil.srv.core.persist.core.repository.JobInfoRepository;
import com.github.devil.srv.core.persist.core.repository.JobInstanceRepository;
import com.github.devil.srv.core.service.JobService;
import com.github.devil.srv.dto.request.NewTaskRequest;
import com.github.devil.srv.dto.response.PageDTO;
import com.github.devil.srv.dto.response.TaskDTO;
import com.github.devil.srv.dto.response.TaskInstanceDTO;
import com.github.devil.srv.service.TaskService;
import com.github.devil.srv.util.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author eric.yao
 * @date 2021/2/18
 **/
@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private JobInfoRepository jobInfoRepository;
    @Resource
    private JobService jobService;
    @Resource
    private JobInstanceRepository instanceRepository;

    @Override
    public Boolean newTask(NewTaskRequest request) {

        JobInfoEntity entity = new JobInfoEntity();
        entity.setLastTriggerTime(null);
        entity.setAppName(request.getAppName());
        entity.setExecuteType(request.getExecuteType());
        entity.setJobStatus(JobStatus.NORMAL);

        if (!request.getTimeType().validExpression(request.getTimeExpression())){
            throw new IllegalArgumentException("请检查时间表达式格式是否错误");
        }

        if (Objects.equals(request.getTaskType(), TaskType.SHELL) && StringUtils.isEmpty(request.getJobMeta())){
            throw new IllegalArgumentException("Shell 脚本需要输入脚本内容");
        }

        // shell 脚本不支持设置参数
        if (Objects.equals(request.getTaskType(), TaskType.SHELL)){
            entity.setJobMeta(request.getJobMeta());
        }else {
            entity.setParams(request.getJobMeta());
        }

        if (StringUtils.isEmpty(request.getTaskName()) && Objects.equals(request.getTaskType(), TaskType.REMOTE_CLIENT)){
            throw new IllegalArgumentException("Task Name 不能为空");
        }

        entity.setNextTriggerTime(request.getTimeType().getNext(new Date(),request.getTimeExpression()));
        entity.setTaskType(request.getTaskType());
        entity.setVersion(0);
        entity.setWorkerHost(request.getWorkHost());
        entity.setTimeVal(request.getTimeExpression());
        entity.setUniqueName(request.getTaskName());
        entity.setTimeType(request.getTimeType());
        entity.setServeHost(MainAkServer.getCurrentHost());
        entity.setDes(request.getDes());
        jobInfoRepository.saveAndFlush(entity);
        return true;
    }

    @Override
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    public void stopAllAndTransfer(String serverHost) {

        if(jobInfoRepository.countByServeHost(serverHost) > 0) {

            jobService.cancelAllWaitTask(serverHost);

            String healthServer = MainAkServer.nextHealthServer();
            int count = jobInfoRepository.transferToAnotherServer(serverHost, healthServer,new Date());

            log.info("transfer task from dead server:[{}] to another server:[{}], task count:[{}]",serverHost,healthServer,count);
        }
    }

    @Override
    public PageDTO<TaskDTO> getTaskPage(String taskName, String appName, Integer pageSize, Integer current) {

        if (current == null || current < 0){
            current = 0;
        }
        Page<JobInfoEntity> page;
        if (taskName == null && appName == null){
            page = jobInfoRepository.findAll(PageRequest.of(current,pageSize, Sort.by(Sort.Direction.DESC,"upt")));
        }else {
            JobInfoEntity entity = new JobInfoEntity();
            entity.setVersion(null);
            entity.setUniqueName(taskName);
            entity.setAppName(appName);
            Example<JobInfoEntity> example = Example.of(entity);
            page = jobInfoRepository.findAll(example,PageRequest.of(current,pageSize, Sort.by(Sort.Direction.DESC,"upt")));
        }
        PageDTO<TaskDTO> pageDTO = new PageDTO<>();
        pageDTO.setPage(current);
        pageDTO.setPageSize(pageSize);
        pageDTO.setTotal(page.getTotalElements());
        pageDTO.setData(page.getContent().stream().map(ConvertUtils::convert).collect(Collectors.toList()));
        return pageDTO;
    }

    @Override
    public PageDTO<TaskInstanceDTO> getInstancePage(String taskName, String appName, Long taskId, Integer pageSize, Integer current) {
        if (current == null || current < 0){
            current = 0;
        }
        Page<InstanceEntity> page;
        if (taskName == null && appName == null && taskId == null){
            page = instanceRepository.findAll(PageRequest.of(current,pageSize,Sort.by(Sort.Direction.DESC,"upt")));
        } else {
            InstanceEntity example = new InstanceEntity();
            example.setVersion(null);
            example.setAppName(appName);
            example.setUniqueName(taskName);
            example.setJobId(taskId);
            page = instanceRepository.findAll(Example.of(example),PageRequest.of(current,pageSize, Sort.by(Sort.Direction.DESC,"upt")));
        }
        PageDTO<TaskInstanceDTO> pageDTO = new PageDTO<>();
        pageDTO.setPage(current);
        pageDTO.setPageSize(pageSize);
        pageDTO.setTotal(page.getTotalElements());
        pageDTO.setData(page.getContent().stream().map(ConvertUtils::convert).collect(Collectors.toList()));
        return pageDTO;
    }
}
