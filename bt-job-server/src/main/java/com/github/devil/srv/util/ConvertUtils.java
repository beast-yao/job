package com.github.devil.srv.util;

import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import com.github.devil.srv.dto.response.TaskDTO;
import com.github.devil.srv.dto.response.TaskInstanceDTO;

/**
 * create by Yao 2021/3/31
 **/
public class ConvertUtils {

    public static TaskDTO convert(JobInfoEntity jobInfoEntity){
        if (jobInfoEntity == null){
            return null;
        }
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setDesc(jobInfoEntity.getDes());
        taskDTO.setExecuteType(jobInfoEntity.getExecuteType());
        taskDTO.setId(jobInfoEntity.getId());
        taskDTO.setTaskType(jobInfoEntity.getTaskType());
        taskDTO.setTaskName(jobInfoEntity.getUniqueName());
        taskDTO.setTimeType(jobInfoEntity.getTimeType());
        taskDTO.setLastTriggerTime(jobInfoEntity.getLastTriggerTime());
        taskDTO.setServeHost(jobInfoEntity.getServeHost());
        taskDTO.setTimeVal(jobInfoEntity.getTimeVal());
        taskDTO.setNextTriggerTime(jobInfoEntity.getNextTriggerTime());
        taskDTO.setCrt(jobInfoEntity.getCrt());
        taskDTO.setAppName(jobInfoEntity.getAppName());
        return taskDTO;
    }

    public static TaskInstanceDTO convert(InstanceEntity entity){
        if (entity == null){
            return null;
        }
        TaskInstanceDTO instanceDTO = new TaskInstanceDTO();
        instanceDTO.setDes(entity.getDes());
        instanceDTO.setInstanceId(entity.getId());
        instanceDTO.setExceptTriggerTime(entity.getExceptTriggerTime());
        instanceDTO.setExecuteStatue(entity.getExecuteStatue());
        instanceDTO.setExecuteEndTime(entity.getExecuteEndTime());
        instanceDTO.setAppName(entity.getAppName());
        instanceDTO.setTaskName(entity.getUniqueName());
        instanceDTO.setExecuteType(entity.getExecuteType());
        instanceDTO.setTaskType(entity.getTaskType());
        instanceDTO.setTimeType(entity.getTimeType());
        instanceDTO.setTimeVal(entity.getTimeVal());
        instanceDTO.setTriggerTime(entity.getTriggerTime());
        instanceDTO.setServeHost(entity.getServeHost());
        return instanceDTO;
    }

}
