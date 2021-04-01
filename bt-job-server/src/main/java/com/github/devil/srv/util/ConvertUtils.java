package com.github.devil.srv.util;

import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import com.github.devil.srv.dto.response.TaskDTO;

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

}
