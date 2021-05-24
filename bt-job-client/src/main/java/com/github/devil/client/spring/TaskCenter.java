package com.github.devil.client.spring;

import com.github.devil.client.process.InvokeProcess;
import com.github.devil.client.process.TaskContext;
import com.github.devil.client.process.TaskContextHolder;
import com.github.devil.client.process.TaskLifecycle;
import com.github.devil.client.spring.process.ShellProcess;
import com.github.devil.common.enums.ResultEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
@Data
@Slf4j
@AllArgsConstructor
public class TaskCenter {

    private static final Map<String, InvokeProcess> invokers = new ConcurrentHashMap<>(64);

    private static InvokeProcess DEFAULT_SHELL = new ShellProcess();

    private static final Map<String, LinkedHashSet<TaskLifecycle>> lifecycles = new ConcurrentHashMap<>();

    public static void registerProcess(String name,InvokeProcess process){
        synchronized (invokers) {
            InvokeProcess res = invokers.putIfAbsent(name, process);
            if (res != null && res != process) {
                throw new IllegalArgumentException("conflict schedule task name");
            }
        }
    }

    public static void registerTaskAspect(String name,TaskLifecycle lifecycle){
        if (lifecycle == null ){
            return;
        }
        if (name == null || name.length() <= 0){
            throw new IllegalArgumentException(String.format("lifeCycle has no valid name,[%s]",lifecycle.getClass().getName()));
        }

        if (!Objects.equals(name,TaskLifecycle.DEFAULT_LIFECYCLE_NAME) && !invokers.containsKey(name)){
            throw new IllegalArgumentException(String.format("lifeCycle has no valid name,has no schedule register,[%s]",lifecycle.getClass().getName()));
        }
        synchronized (lifecycles){
            LinkedHashSet<TaskLifecycle> lifecycles =  TaskCenter.lifecycles.get(name);
            if (lifecycles == null){
                lifecycles = new LinkedHashSet<>();
            }
            lifecycles.add(lifecycle);
            TaskCenter.lifecycles.put(name,lifecycles);
        }
    }


    public static void beforeProcess(TaskContext taskContext){
        getAspect(taskContext.getName()).forEach(aspect -> aspect.beforeTask(taskContext));
    }

    private static void afterProcess(TaskContext taskContext,ResultEnums resultEnums,Throwable throwable){
        getAspect(taskContext.getName()).forEach(aspect -> aspect.afterTask(taskContext,resultEnums,throwable));
    }

    public static ResultEnums runProcess(TaskContext taskContext){
        try {
            if (log.isDebugEnabled()){
                log.debug("find invoke process to run task,taskName[{}],instanceId[{}]",taskContext.getName(),taskContext.getInstanceId());
            }
            InvokeProcess process = getProcess(taskContext);

            Objects.requireNonNull(process,String.format("unique name %s is error cannot find process",taskContext.getName()));
            TaskContextHolder.register(taskContext);
            ResultEnums res =  process.run(taskContext);
            afterProcess(taskContext,res,null);
            return res;
        }catch (Exception e){
            log.error("execute invoke error,uniqueName:{}",taskContext.getName(),e);
            taskContext.getLogger().error("execute invoke error,uniqueName:{}",taskContext.getName(),e);
            afterProcess(taskContext,ResultEnums.F,e);
            return ResultEnums.F;
        }finally {
            TaskContextHolder.remove();
        }
    }

    private static InvokeProcess getProcess(TaskContext taskContext){
        switch (taskContext.getTaskType()){
            case SHELL:
                return DEFAULT_SHELL;
            case REMOTE_CLIENT:
                return invokers.get(taskContext.getName());
            default:
                return null;
        }
    }

    private static Set<TaskLifecycle> getAspect(String taskName){
        Set<TaskLifecycle> lifecycles = new LinkedHashSet<>();
        Set<TaskLifecycle> defaults = TaskCenter.lifecycles.get(TaskLifecycle.DEFAULT_LIFECYCLE_NAME);
        if (defaults != null){
            lifecycles.addAll(defaults);
        }

        if (taskName != null && !Objects.equals(taskName,"")){
            Set<TaskLifecycle> aspect = TaskCenter.lifecycles.get(taskName);
            if (aspect != null){
                lifecycles.addAll(aspect);
            }
        }
        return lifecycles;
    }
}
