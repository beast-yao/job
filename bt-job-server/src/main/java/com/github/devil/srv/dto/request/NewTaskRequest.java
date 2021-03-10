package com.github.devil.srv.dto.request;

import com.github.devil.common.enums.ExecuteType;
import com.github.devil.common.enums.TaskType;
import com.github.devil.common.enums.TimeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author eric.yao
 * @date 2021/2/18
 **/
@Data
@ApiModel
public class NewTaskRequest {

    @ApiModelProperty(required = true,value = "task name")
    @NotBlank(message = "task name should not be null or blank")
    private String taskName;

    @ApiModelProperty(required = true,value = "time type")
    @NotNull(message = "time type should not be null")
    private TimeType timeType;

    @ApiModelProperty(required = true,value = "time expression")
    @NotBlank(message = "time expression should not be null or blank")
    private String timeExpression;

    @ApiModelProperty(required = true,value = "execute type")
    @NotNull(message = "execute type should not be null")
    private ExecuteType executeType;

    @ApiModelProperty(required = true,value = "task type")
    @NotNull(message = "task type should not be null")
    private TaskType taskType;

    @ApiModelProperty(required = true,value = "app name")
    @NotBlank(message = "app name should not be null or blank")
    private String appName;

    private String workHost;
}
