package com.github.devil.core.ha;

import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/1/19
 **/
@Data
public class AppInfo {

    private String appName;

    private long lastReceive;

    private String appHost;

    private long interval;

}
