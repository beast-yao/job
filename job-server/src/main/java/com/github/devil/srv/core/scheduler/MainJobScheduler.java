package com.github.devil.srv.core.scheduler;

import static com.github.devil.srv.core.exception.ExceptionConstants.*;
import com.github.devil.srv.core.exception.JobException;
import com.github.devil.srv.timer.TimerFuture;
import com.github.devil.srv.timer.TimerTask;
import com.github.devil.srv.timer.TimerWheel;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author eric.yao
 * @date 2021/1/19
 **/
@Slf4j
public class MainJobScheduler {

    /**
     * 时间轮
     */
    public final static TimerWheel TIMER = new TimerWheel();


    private static final Map<Long, TimerFuture> FUTURE_MAP = Maps.newConcurrentMap();

    /**
     * scheduler a job
     * @param id unique id
     * @param delay delay time
     * @param timerTask timerTask to run
     * @return
     */
    public static TimerFuture schedule(Long id, long delay, TimerTask timerTask){
        if (delay <= 0){
            timerTask.run();
            return null;
        }
        TimerFuture future = TIMER.delay(()->{
            FUTURE_MAP.remove(id);
            timerTask.run();
        },delay, TimeUnit.MILLISECONDS);

        FUTURE_MAP.put(id,future);
        return future;
    }

    /**
     * cancel a job maybe throw a exception
     * @param id unique id
     */
    public static void cancel(Long id){
        Optional.ofNullable(FUTURE_MAP.get(id)).orElseThrow(() -> new JobException(NO_JOB_TO_CANCEL)).cancel();
    }


    public static void stop(){
        /**
         * stop the timer
         */
        TIMER.stop();

        /**
         * cancel all the task that not execute
         */
        cancelAllTask();
    }

    /**
     * cancel all task
     * @return all cancel instanceIds
     */
    public static Set<Long> cancelAllTask(){
        Set<Long> instances = Sets.newHashSet();
        for (Map.Entry<Long, TimerFuture> futureEntry : FUTURE_MAP.entrySet()) {
            Long instanceId = futureEntry.getKey();
            try {
                futureEntry.getValue().cancel();
                instances.add(instanceId);
            }catch (Exception e){
                log.warn("stop task fail,taskId:[{}]",instanceId);
            }
        }
        return instances;
    }
}
