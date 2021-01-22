package com.github.devil.srv.core.scheduler;

import static com.github.devil.srv.core.exception.ExceptionConstants.*;
import com.github.devil.srv.core.exception.JobException;
import com.github.devil.srv.timer.TimerFuture;
import com.github.devil.srv.timer.TimerTask;
import com.github.devil.srv.timer.TimerWheel;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author eric.yao
 * @date 2021/1/19
 **/
public class MainJobScheduler {

    /**
     * 时间轮
     */
    private final static TimerWheel TIMER = new TimerWheel();


    private static final Map<Long, TimerFuture> FUTURE_MAP = Maps.newConcurrentMap();

    /**
     * scheduler a job
     * @param id unique id
     * @param delay delay time
     * @param timerTask timerTask to run
     * @return
     */
    public static TimerFuture schedule(Long id, long delay, TimerTask timerTask){
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
}
