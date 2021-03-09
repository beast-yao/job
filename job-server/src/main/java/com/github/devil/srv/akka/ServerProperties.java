package com.github.devil.srv.akka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

/**
 * @author eric.yao
 * @date 2021/2/4
 **/
@Data
@ConfigurationProperties(prefix = ServerProperties.PRE_FIX)
public class ServerProperties {

    public final static String PRE_FIX = "main.job";

    private String host;

    private Integer port = 10010;

    private List<String> memberList;

    /**
     * 执行任务结果最长等待时间
     */
    private Integer maxExecuteWaitSeconds;

}
