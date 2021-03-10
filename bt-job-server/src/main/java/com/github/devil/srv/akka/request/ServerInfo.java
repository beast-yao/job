package com.github.devil.srv.akka.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.devil.common.serialization.JobSerializable;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/1/20
 **/
@Data
public class ServerInfo implements JobSerializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private long receiverTime;

    private int surviveCount;

    private String serverHost;
}
