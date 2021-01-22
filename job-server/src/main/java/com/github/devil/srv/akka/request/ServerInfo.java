package com.github.devil.srv.akka.request;

import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/1/20
 **/
@Data
public class ServerInfo {

    private long receiverTime;

    private int surviveCount;

}
