package com.github.devil.srv.health;

import com.github.devil.srv.core.scheduler.MainJobScheduler;
import com.github.devil.srv.timer.TimerWheel;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

/**
 * @author eric.yao
 * @date 2021/1/26
 **/
@Component
public class TaskHealthIndicator extends AbstractHealthIndicator {

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
         builder.status(Status.UP);
         if (MainJobScheduler.TIMER.isStart()){
             builder
                 .withDetail("startTime", MainJobScheduler.TIMER.getStartTime())
                 .withDetail("stat","START");
         }else {
             builder.withDetail("stat","UN-START");
         }
        builder.build();
    }
}
