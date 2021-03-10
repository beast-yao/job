package com.github.devil.srv.core.notify.event;

import com.github.devil.srv.core.exception.JobException;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author eric.yao
 * @date 2021/1/29
 **/
@Data
@Accessors(chain = true)
public class JobExecuteFailEvent extends Event {

    private Long instanceId;

    private Long jobId;

    private String workHost;

    private JobException exception;

    private Long workInstanceId;

    private String appName;
}
