package com.github.devil.srv.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.common.enums.ExecuteType;
import com.github.devil.common.enums.TaskType;
import com.github.devil.common.enums.TimeType;
import lombok.Data;

import java.util.Date;

/**
 * create by Yao 2021/5/24
 **/
@Data
public class TaskInstanceDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long instanceId;

    private String taskName;

    private String des;

    private TimeType timeType;

    private String timeVal;

    private ExecuteType executeType;

    private ExecuteStatue executeStatue;

    private TaskType taskType;

    private Date exceptTriggerTime;

    private Date triggerTime;

    private Date executeEndTime;

    private String appName;

    private String serveHost;
}
