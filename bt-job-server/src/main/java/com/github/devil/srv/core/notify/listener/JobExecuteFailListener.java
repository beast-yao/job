package com.github.devil.srv.core.notify.listener;

import com.github.devil.srv.core.Constants;
import com.github.devil.srv.core.alarm.Message;
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

    @Override
    public void onEvent(JobExecuteFailEvent event) {
        Message message = Message.builder()
                .content(buildMessage(event))
                .title(Constants.FAIL_JOB_TITLE).build();
        SpringContextHolder.getBean(AlarmService.class).alarm(message);
    }

    private String buildMessage(JobExecuteFailEvent event){
        return String.format("任务编号:%s,任务实例:%s,AppName:[%s],执行器执行编号 %s ,执行器地址:%s,异常信息:%s",
                        event.getJobId(),
                        event.getInstanceId(),
                        event.getAppName(),
                        event.getWorkHost(),
                        event.getWorkInstanceId(),
                        event.getException() == null?"":event.getException().getMessage());
    }
}
