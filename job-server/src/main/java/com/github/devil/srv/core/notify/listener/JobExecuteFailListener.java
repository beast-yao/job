package com.github.devil.srv.core.notify.listener;

import com.github.devil.srv.config.alarm.Message;
import com.github.devil.srv.core.SpringContextHolder;
import com.github.devil.srv.core.alarm.AlarmService;
import com.github.devil.srv.core.notify.event.JobExecuteFailEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author eric.yao
 * @date 2021/1/29
 **/
@Slf4j
public class JobExecuteFailListener implements Listener<JobExecuteFailEvent> {

    private final static String FAIL_JOB_TITLE = "任务执行失败";

    @Override
    public void onEvent(JobExecuteFailEvent event) {
        log.error("{}",event);
        Message message = Message.builder()
                .content(buildMessage(event))
                .title(FAIL_JOB_TITLE).build();
        SpringContextHolder.getBean(AlarmService.class).alarm(message);
    }

    private String buildMessage(JobExecuteFailEvent event){
        return String.format("任务编号:%s,任务实例:%s,执行器地址:%s,异常信息:%s",
                        event.getJobId(),
                        event.getInstanceId(),
                        event.getWorkHost(),
                        event.getException() == null?"":event.getException().getMessage());
    }
}
