package com.github.devil.srv.core.notify.listener;

import com.github.devil.srv.core.SpringContextHolder;
import com.github.devil.srv.core.alarm.AlarmService;
import com.github.devil.srv.core.alarm.Message;
import com.github.devil.srv.core.notify.event.SchedulerJobErrorEvent;

/**
 * @author eric.yao
 * @date 2021/3/4
 **/
public class SchedulerJobErrorListener implements Listener<SchedulerJobErrorEvent> {
    @Override
    public void onEvent(SchedulerJobErrorEvent event) {
        Message message = Message
                            .builder()
                            .content(String.format("调度任务失败，任务编号[%s]，实例编号[%s]，描述信息[%s]",event.getJobId(),event.getInstanceId(),event.getDescribe()))
                            .title("任务调度失败")
                            .build();
        SpringContextHolder.getBean(AlarmService.class).alarm(message);
    }
}
