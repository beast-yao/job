package com.github.devil.srv.core.notify.event;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Yao
 * @date 2021/3/10
 **/
@Data
@Builder
public class ExecuteTooLongTimeEvent extends Event {

    private List<Long> instanceIds;

}
