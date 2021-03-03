package com.github.devil.srv.core.notify.listener;

import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.core.SpringContextHolder;
import com.github.devil.srv.core.notify.event.ServeUnReceiveEvent;
import com.github.devil.srv.core.scheduler.MainJobScheduler;
import com.github.devil.srv.service.TaskService;
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
        log.warn("server has down [{}]",event.getServerHost());
        if (Objects.equals(event.getServerHost(), MainAkServer.getCurrentHost())){
            /**
             *   本级服务网络问题,自检失败
             *   1. 修改状态为异常状态,暂停任务推送
             */
            MainAkServer.stateFail();

            // 2. 取消定时器中待执行的任务
            MainJobScheduler.cancelAllTask();

            // 3. 将当前服务任务移交给其他服务
            SpringContextHolder.getBean(TaskService.class).stopAllAndTransfer();
        } else {
            //todo handle server job
        }
    }
}
