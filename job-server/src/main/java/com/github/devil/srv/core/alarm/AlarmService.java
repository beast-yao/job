package com.github.devil.srv.core.alarm;

/**
 * @author eric.yao
 * @date 2021/2/26
 **/
public interface AlarmService {

    /**
     * 服务告警
     * @param message
     */
    void alarm(Message message);

    /**
     * 名称
     * @return
     */
    String name();
}
