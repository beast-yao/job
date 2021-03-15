package com.github.devil.srv.akka.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.devil.common.serialization.JobSerializable;
import lombok.Data;

import java.util.Map;

/**
 * @author eric.yao
 * @date 2021/1/20
 **/
@Data
public class ServerInfo implements JobSerializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private long receiverTime;

    private Map<String,Object> metaData;

    private String serverHost;

}
