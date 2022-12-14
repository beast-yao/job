package com.github.devil.srv.core.notify.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author eric.yao
 * @date 2021/3/4
 **/
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SchedulerJobErrorEvent extends Event {

    private Long jobId;

    private Long instanceId;

    private String describe;

}
