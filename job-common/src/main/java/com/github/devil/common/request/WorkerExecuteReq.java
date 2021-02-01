package com.github.devil.common.request;

import com.github.devil.common.enums.TaskType;
import com.github.devil.common.serialization.JobSerializable;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/1/27
 **/
@Data
public class WorkerExecuteReq implements JobSerializable {

    private Long instanceId;

    private Long jobId;

    private Long workInstanceId;

    private String params;

    private String jobMeta;

    private String uniqueName;

    private String serverHost;

    private TaskType taskType;

}
