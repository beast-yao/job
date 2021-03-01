package com.github.devil.srv.core.notify.listener;

import com.github.devil.srv.core.notify.event.WorkerDownEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * worker down
 * @author eric.yao
 * @date 2021/2/1
 **/
@Slf4j
public class WorkerDownListener implements Listener<WorkerDownEvent> {

    @Override
    public void onEvent(WorkerDownEvent event) {
        //todo notify alarm
        log.info("{}",event);
    }
}
