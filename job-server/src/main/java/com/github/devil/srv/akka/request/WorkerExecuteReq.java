package com.github.devil.srv.akka.request;

import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/1/25
 **/
@Data
public class WorkerExecuteReq {

    private Long instanceId;

    private Long jobId;

    private Long workInstanceId;

    private String params;

    private String jobMeta;

    private String uniqueName;

}
