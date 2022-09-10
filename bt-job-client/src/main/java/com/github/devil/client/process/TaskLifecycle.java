package com.github.devil.client.process;

import com.github.devil.client.exception.RejectException;
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
     * if null or empty that all task will be used
     * @return name
     */
    default List<String> name(){
        return null;
    }

    /**
     * beforeTask
     * @param taskContext 任务执行上下文
     */
    void beforeTask(TaskContext taskContext) throws RejectException;

    /**
     * afterTask
     * @param taskContext 任务执行上下文
     * @param enums 执行状态
     * @param throwable 执行异常信息
     */
    void afterTask(TaskContext taskContext, ResultEnums enums,Throwable throwable);
}
