package com.github.devil.srv.core.notify.event;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Yao
 * @date 2021/3/10
 **/
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ExecuteTooLongTimeEvent extends Event {

    private List<Long> instanceIds;

}
