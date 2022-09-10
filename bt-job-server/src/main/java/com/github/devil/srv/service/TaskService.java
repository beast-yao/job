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
     * @param request 新增任务请求信息
     * @return 创建结果
     */
    Boolean newTask(NewTaskRequest request);

    /**
     * 停止所有任务，并将当前服务任务进行转移
     * @param serverHost 指定服务地址
     */
    void stopAllAndTransfer(String serverHost);

    /**
     * 分页查询任务信息
     * @param taskName 任务名
     * @param appName 服务名
     * @param pageSize 数量
     * @param current 页码
     * @return 任务信息
     */
    PageDTO<TaskDTO> getTaskPage(String taskName,String appName,Integer pageSize,Integer current);

    /**
     * 分页查询任务执行实例信息
     * @param taskName 任务名
     * @param appName 服务名
     * @param pageSize 数量
     * @param current 页码
     * @return 任务执行实例
     */
    PageDTO<TaskInstanceDTO> getInstancePage(String taskName, String appName, Long taskId,Integer pageSize, Integer current);
}
