package com.github.devil.srv.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.devil.common.enums.ExecuteType;
import com.github.devil.common.enums.TaskType;
import com.github.devil.common.enums.TimeType;
import com.github.devil.srv.config.jackson.BaseEnumSerial;
import lombok.Data;

import java.util.Date;

/**
 * create by Yao 2021/3/31
 **/
@Data
public class TaskDTO {

    private Long id;

    private String appName;

    private String taskName;

    private String desc;

    @JsonSerialize(using = BaseEnumSerial.class)
    private TimeType timeType;

    private TaskType taskType;

    private String timeVal;

    @JsonSerialize(using = BaseEnumSerial.class)
    private ExecuteType executeType;

    private String serveHost;

    private Date lastTriggerTime;

    private Date nextTriggerTime;

    private Date crt;
}
