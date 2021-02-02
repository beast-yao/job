package com.github.devil.srv.core.notify.listener;

import com.github.devil.srv.core.notify.event.ServeUnReceiveEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author eric.yao
 * @date 2021/1/29
 **/
@Slf4j
public class ServeUnReceiveListener implements Listener<ServeUnReceiveEvent> {

    @Override
    public void onEvent(ServeUnReceiveEvent event) {
        //todo handle server job
        log.info("{}",event);
    }
}
