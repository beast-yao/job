package com.github.devil.common.dto;

import com.github.devil.common.enums.LogLevel;
import com.github.devil.common.serialization.JobSerializable;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
@Data
public class LogContent implements JobSerializable {

    @NotNull(message = "log workInstanceId is required")
    private Long workInstanceId;

    @NotNull(message = "log jobId is required")
    private Long jobId;

    @NotNull(message = "log instanceId is required")
    private Long instanceId;

    @NotNull(message = "log loggingTime is required")
    private Date loggingTime;

    @NotNull(message = "log loggingContent is required")
    private String loggingContent;

    @NotNull(message = "log logLevel is required")
    private LogLevel logLevel;

    @NotNull(message = "log workAddress is required")
    private String workAddress;

    private String serverHost;

}
