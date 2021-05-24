package com.github.devil.srv.controller;

import com.github.devil.common.CommonConstants;
import com.github.devil.srv.dto.request.NewTaskRequest;
import com.github.devil.srv.dto.response.PageDTO;
import com.github.devil.srv.dto.response.Resp;
import com.github.devil.srv.dto.response.TaskDTO;
import com.github.devil.srv.dto.response.TaskInstanceDTO;
import com.github.devil.srv.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author eric.yao
 * @date 2021/2/18
 **/
@Api(tags = "Task")
@RestController
@RequestMapping(CommonConstants.BASE_CONTROLLER_PATH+CommonConstants.BASE_TASK_CONTROLLER_PATH)
public class TaskController {

    @Resource
    private TaskService taskService;

    @PostMapping
    @ApiOperation(httpMethod = "POST",value = "new task")
    public Resp<Boolean> newTask(@Validated @RequestBody NewTaskRequest newTaskRequest){
        return new Resp<>(0, taskService.newTask(newTaskRequest));
    }

    @GetMapping("/page")
    @ApiOperation(httpMethod = "GET",value = "task page")
    public Resp<PageDTO<TaskDTO>> getTask(@RequestParam(name = "taskName",required = false) String taskName,
                                          @RequestParam(name = "appName",required = false) String appName,
                                          @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                          @RequestParam(name = "current",required = false,defaultValue = "0") Integer current){
        return new Resp<>(0,taskService.getTaskPage(taskName,appName,pageSize,current));
    }

    @GetMapping("/instance/page")
    @ApiOperation(httpMethod = "GET",value = "task page")
    public Resp<PageDTO<TaskInstanceDTO>> getTaskInstance(@RequestParam(name = "taskName",required = false) String taskName,
                                                          @RequestParam(name = "appName",required = false) String appName,
                                                          @RequestParam(name = "taskId",required = false) Long taskId,
                                                          @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                                          @RequestParam(name = "current",required = false,defaultValue = "0") Integer current){
        return new Resp<>(0,taskService.getInstancePage(taskName,appName,taskId,pageSize,current));
    }

}
