package com.github.devil.srv.service;

import com.github.devil.srv.dto.request.NewTaskRequest;

/**
 * @author eric.yao
 * @date 2021/2/18
 **/
public interface TaskService {

    /**
     * 新增定时任务
     * @param request
     * @return
     */
    Boolean newTask(NewTaskRequest request);

}
