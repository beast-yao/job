package com.github.devil.srv.core.notify.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author eric.yao
 * @date 2021/2/1
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WorkerDownEvent extends Event {

    private String appName;

    private String workHost;

}
