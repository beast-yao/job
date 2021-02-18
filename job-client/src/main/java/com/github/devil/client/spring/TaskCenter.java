package com.github.devil.client.spring;

import com.github.devil.client.process.InvokeProcess;
import com.github.devil.client.process.TaskContext;
import com.github.devil.client.process.TaskContextHolder;
import com.github.devil.common.enums.ResultEnums;
import com.github.devil.common.request.WorkerExecuteReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
@Data
@Slf4j
@AllArgsConstructor
public class TaskCenter {

    /**
     * unique and process
     */
    private static Map<String, InvokeProcess> invokers = new ConcurrentHashMap<>(64);

    public static void registerProcess(String name,InvokeProcess process){
        InvokeProcess res = invokers.putIfAbsent(name,process);
        if (res != null && res != process){
            throw new IllegalArgumentException("conflict schedule task name");
        }
    }

    public static ResultEnums runProcess(WorkerExecuteReq req){
        TaskContext taskContext = new TaskContext();
        taskContext.setInstanceId(req.getInstanceId());
        taskContext.setJobId(req.getJobId());
        taskContext.setParam(req.getParams());
        taskContext.setWorkInstanceId(req.getWorkInstanceId());
        taskContext.setServer(req.getServerHost());
        taskContext.setName(req.getUniqueName());

        try {
            if (log.isDebugEnabled()){
                log.debug("receive an req to execute job,name:{}",req.getUniqueName());
            }
            InvokeProcess process = invokers.get(req.getUniqueName());
            Objects.requireNonNull(process,String.format("unique name %s is error cannot find process",req.getUniqueName()));
            TaskContextHolder.register(taskContext);
            return process.run(taskContext);
        }catch (Exception e){
            taskContext.getLogger().error("execute invoke error,uniqueName:{}",req.getUniqueName(),e);
            return ResultEnums.F;
        }finally {
            TaskContextHolder.remove();
        }
    }

}
