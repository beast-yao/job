package com.github.devil.srv.akka.request;

import com.github.devil.common.serialization.JobSerializable;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/1/20
 **/
@Data
public class ServerInfo implements JobSerializable {

    private long receiverTime;

    private int surviveCount;

}
