package com.github.devil.srv.core.notify.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/1/29
 **/
@Data
@AllArgsConstructor
public class ServeUnReceiveEvent extends Event {

    private String serverHost;
}
