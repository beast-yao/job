package com.github.devil.srv.akka;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

/**
 * @author eric.yao
 * @date 2021/2/4
 **/
@Data
@ConfigurationProperties(prefix = ServerProperties.PRE_FIX)
public class ServerProperties implements InitializingBean {

    public final static String PRE_FIX = "main.job";

    private String host;

    private Integer port = 10010;

    private List<String> memberList;

    /**
     * 执行任务结果最长等待时间
     */
    private Integer maxExecuteWaitSeconds;

    /**
     * 保留最近执行任务次数 > 0 生效
     * 超过次数的历史任务实例会被删除，日志也会被删除
     * 合理设置释放磁盘资源
     */
    private Integer maxTaskInstance = -1;

    @Override
    public void afterPropertiesSet() throws Exception {
        MainAkServer.start(this);
    }
}
