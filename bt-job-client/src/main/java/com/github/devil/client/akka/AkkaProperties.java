package com.github.devil.client.akka;

import lombok.Data;

import java.util.List;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
@Data
public class AkkaProperties {

    public final static String PREFIX = "job.client";

    private String host;

    private int port = 10000;

    private String appName;

    private List<String> servers;
}
