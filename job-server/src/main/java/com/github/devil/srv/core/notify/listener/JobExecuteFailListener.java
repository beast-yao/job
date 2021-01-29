package com.github.devil.srv.core.notify.listener;

import com.github.devil.srv.core.notify.event.JobExecuteFailEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author eric.yao
 * @date 2021/1/29
 **/
@Slf4j
public class JobExecuteFailListener implements Listener<JobExecuteFailEvent> {

    @Override
    public void onEvent(JobExecuteFailEvent event) {
        log.info("{}",event);
    }
}
