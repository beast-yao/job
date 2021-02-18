package com.github.devil.srv.controller;

import com.github.devil.common.CommonConstants;
import com.github.devil.srv.dto.request.NewTaskRequest;
import com.github.devil.srv.dto.response.Resp;
import com.github.devil.srv.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author eric.yao
 * @date 2021/2/18
 **/
@Api(tags = "Task")
@RestController
@RequestMapping(CommonConstants.BASE_CONTROLLER_PATH)
public class TaskController {

    @Resource
    private TaskService taskService;

    @PostMapping
    @ApiOperation(httpMethod = "POST",value = "new task")
    public Resp<Boolean> newTask(@Validated @RequestBody NewTaskRequest newTaskRequest){
        return new Resp<>(0, taskService.newTask(newTaskRequest));
    }

}
