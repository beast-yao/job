package com.github.devil.srv.core.notify.event;

import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/2/1
 **/
@Data
public class WorkerDownEvent extends Event {

    private String appName;

    private String workHost;

}
