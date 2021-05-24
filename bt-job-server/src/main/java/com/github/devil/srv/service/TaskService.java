package com.github.devil.srv.service;

import com.github.devil.srv.dto.request.NewTaskRequest;
import com.github.devil.srv.dto.response.PageDTO;
import com.github.devil.srv.dto.response.TaskDTO;
import com.github.devil.srv.dto.response.TaskInstanceDTO;

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

    /**
     * 停止所有任务，并将当前服务任务进行转移
     * @param serverHost
     */
    void stopAllAndTransfer(String serverHost);

    /**
     * 分页查询
     * @param taskName
     * @param appName
     * @param pageSize
     * @param current
     * @return
     */
    PageDTO<TaskDTO> getTaskPage(String taskName,String appName,Integer pageSize,Integer current);

    /**
     * 分页查询
     * @param taskName
     * @param appName
     * @param pageSize
     * @param current
     * @return
     */
    PageDTO<TaskInstanceDTO> getInstancePage(String taskName, String appName, Long taskId,Integer pageSize, Integer current);
}
