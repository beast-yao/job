package com.github.devil.srv.core.notify.listener;

import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.core.notify.event.ServeUnReceiveEvent;
import com.github.devil.srv.core.scheduler.MainJobScheduler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * server down need to process task
 * @author eric.yao
 * @date 2021/1/29
 **/
@Slf4j
public class ServeUnReceiveListener implements Listener<ServeUnReceiveEvent> {

    @Override
    public void onEvent(ServeUnReceiveEvent event) {
        log.info("{}",event);
        if (Objects.equals(event.getServerHost(), MainAkServer.getCurrentHost())){
            //todo stop schedule and stop the wait task to cancel
        } else {
            //todo handle server job
        }
    }
}
