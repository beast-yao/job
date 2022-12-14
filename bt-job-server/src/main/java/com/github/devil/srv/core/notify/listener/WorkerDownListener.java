package com.github.devil.srv.core.notify.listener;

import com.github.devil.srv.core.Constants;
import com.github.devil.srv.core.SpringContextHolder;
import com.github.devil.srv.core.alarm.AlarmService;
import com.github.devil.srv.core.alarm.Message;
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

        log.warn("find an work down,appName:[{}],wrkHost: [{}]",event.getAppName(),event.getWorkHost());
        Message message = Message.builder()
                        .content(buildMessage(event))
                        .title(Constants.WORK_DOWN_TITLE)
                        .build();
        SpringContextHolder.getBean(AlarmService.class).alarm(message);

    }

    private String buildMessage(WorkerDownEvent event){
        return String.format("服务名称:%s,下线节点:%s",
                event.getAppName(),
                event.getWorkHost()
                );
    }

}
