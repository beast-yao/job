package com.github.devil.srv.core.notify.listener;

import java.util.stream.Collectors;

import com.github.devil.srv.core.Constants;
import com.github.devil.srv.core.SpringContextHolder;
import com.github.devil.srv.core.alarm.AlarmService;
import com.github.devil.srv.core.alarm.Message;
import com.github.devil.srv.core.notify.event.ExecuteTooLongTimeEvent;

/**
 * @author Yao
 * @date 2021/3/10
 **/
public class ExecuteTooLongTimeListener implements Listener<ExecuteTooLongTimeEvent> {

    @Override
    public void onEvent(ExecuteTooLongTimeEvent event) {

        //todo maybe should set job status

        SpringContextHolder.getBean(AlarmService.class).alarm(Message.builder()
                .title(Constants.EXECUTE_TIMEOUT_TITLE)
                .content(String.format("任务执行时间超过给定时间，任务实例编号:[%s]",event.getInstanceIds().stream()
                        .map(String::valueOf).collect(Collectors.joining(","))))
                .build()
        );
    }
}
