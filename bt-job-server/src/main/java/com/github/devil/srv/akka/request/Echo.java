package com.github.devil.srv.akka.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.devil.common.serialization.JobSerializable;
import com.github.devil.srv.akka.MainAkServer;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/1/20
 **/
@Data
public class Echo implements JobSerializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private long sendTime;

    private String currentServer;

    public Echo(){
        this.sendTime = System.currentTimeMillis();
        this.currentServer = MainAkServer.getCurrentHost();
    }

}
