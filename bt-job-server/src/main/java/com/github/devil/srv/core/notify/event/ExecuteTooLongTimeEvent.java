package com.github.devil.srv.core.notify.event;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * @author Yao
 * @date 2021/3/10
 **/
@Data
@Builder
public class ExecuteTooLongTimeEvent extends Event {

    private List<Long> instanceIds;

}
