package com.github.devil.client.process;

import com.github.devil.common.enums.ResultEnums;

import java.util.List;

/**
 * @author eric.yao
 * @date 2021/3/5
 **/
public interface TaskLifecycle {

    String DEFAULT_LIFECYCLE_NAME = "#default_lifecycle_for_all_task";

    /**
     * return the taskName that should use this lifecycle
     * if null or empty that all task will be use
     * @return name
     */
    default List<String> name(){
        return null;
    }

    /**
     * beforeTask
     * @param taskContext
     */
    void beforeTask(TaskContext taskContext);

    /**
     * afterTask
     * @param taskContext
     * @param enums
     * @param throwable
     */
    void afterTask(TaskContext taskContext, ResultEnums enums,Throwable throwable);
}
