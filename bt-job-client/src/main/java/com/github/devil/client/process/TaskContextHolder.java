package com.github.devil.client.process;

/**
 * @author eric.yao
 * @date 2021/2/3
 **/
public class TaskContextHolder {

    private final static ThreadLocal<TaskContext> TASK_CONTEXT_HOLDER = new ThreadLocal<>();

    public static void register(TaskContext taskContext){
        TASK_CONTEXT_HOLDER.set(taskContext);
    }

    public static void remove(){
        TASK_CONTEXT_HOLDER.remove();
    }

    public static TaskContext get(){
        return TASK_CONTEXT_HOLDER.get();
    }
}
