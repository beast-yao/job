package com.github.devil.srv.core.jmx;

import com.github.devil.srv.core.scheduler.MainJobScheduler;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

/**
 * @author eric.yao
 * @date 2021/3/1
 **/
@Component
@ManagedResource(objectName = "com.github.devil.srv.core.jmx:name=timer")
public class TimerMBean {

    @ManagedOperation
    public void cancelTask(Long id){
        MainJobScheduler.cancel(id);
    }

    @ManagedOperation
    public Long getStartTime(){
        return MainJobScheduler.TIMER.getStartTime();
    }
}
