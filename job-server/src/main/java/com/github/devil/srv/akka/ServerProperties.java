package com.github.devil.srv.akka;

import lombok.Data;

import java.util.List;

/**
 * @author eric.yao
 * @date 2021/2/4
 **/
@Data
public class ServerProperties {

    public final static String PRE_FIX = "main.job";

    private String host;

    private Integer port = 10010;

    private List<String> memberList;

}
