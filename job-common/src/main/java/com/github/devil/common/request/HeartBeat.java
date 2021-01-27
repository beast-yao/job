package com.github.devil.common.request;

import com.github.devil.common.serialization.JobSerializable;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/1/27
 **/
@Data
public class HeartBeat implements JobSerializable {

    private String appName;

    private String workerAddress;

    private long timeStamp;

}
