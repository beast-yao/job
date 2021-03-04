package com.github.devil.srv.core.notify.event;

import lombok.Builder;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/3/4
 **/
@Data
@Builder
public class SchedulerJobErrorEvent extends Event {

    private Long jobId;

    private Long instanceId;

    private String describe;

}
