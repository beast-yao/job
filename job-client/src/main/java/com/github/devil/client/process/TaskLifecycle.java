package com.github.devil.client.process;

import com.github.devil.common.enums.ResultEnums;

/**
 * @author eric.yao
 * @date 2021/3/5
 **/
public interface TaskLifecycle {

    /**
     * return the taskName that should use this lifecycle
     * if null or empty that all task will be use
     * @return name
     */
    default String name(){
        return "";
    }

    /**
     * beforeTask
     * @param taskContext
     */
    public void beforeTask(TaskContext taskContext);

    /**
     * afterTask
     * @param taskContext
     * @param enums
     * @param throwable
     */
    public void afterTask(TaskContext taskContext, ResultEnums enums,Throwable throwable);
}
