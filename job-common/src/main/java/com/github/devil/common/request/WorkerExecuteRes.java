package com.github.devil.common.request;

import com.github.devil.common.enums.ResultEnums;
import com.github.devil.common.serialization.JobSerializable;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/2/4
 **/
@Data
public class WorkerExecuteRes implements JobSerializable {

    private Long instanceId;

    private Long jobId;

    private Long workInstanceId;

    private ResultEnums result;

}
