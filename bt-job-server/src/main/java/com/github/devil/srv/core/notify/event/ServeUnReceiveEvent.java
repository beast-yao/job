package com.github.devil.srv.core.notify.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author eric.yao
 * @date 2021/1/29
 **/
@Data
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ServeUnReceiveEvent extends Event {

    private String serverHost;
}
