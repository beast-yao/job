package com.github.devil.srv.core;

import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author eric.yao
 * @date 2021/1/29
 **/
@Slf4j
public class MainThreadUtil {
    /**
     * 定时触发器.
     */
    private final static ScheduledExecutorService SCHEDULE = new ScheduledThreadPoolExecutor(10,
                                                                    new NamedThreadFactory("SCHEDULE"));

    public final static ScheduledExecutorService JOB_PUSH = new ScheduledThreadPoolExecutor(1,
                                                                    new NamedThreadFactory("JOB-PUSH"));

    public final static ThreadPoolExecutor GLOBAL = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                                        Runtime.getRuntime().availableProcessors() * 4,
                                            60,
                                                        TimeUnit.SECONDS,
                                                        new SynchronousQueue<>(),
                                                        new NamedThreadFactory("GLOBAL"));

    public static void shutdown(){

        if (!JOB_PUSH.isShutdown()){
            JOB_PUSH.shutdown();
        }

        if (!GLOBAL.isShutdown()){
            GLOBAL.shutdown();
        }

        if (!SCHEDULE.isShutdown()){
            SCHEDULE.shutdown();
        }
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable,long delay,long rate,TimeUnit unit){
       return SCHEDULE.scheduleAtFixedRate(() -> {
            try {
                runnable.run();
            }catch (Exception e){
                log.error("execute schedule rate fail,",e);
            }
        },delay,rate,unit);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable,
                                                     long initialDelay,
                                                     long delay,
                                                     TimeUnit unit){
        return SCHEDULE.scheduleWithFixedDelay(() -> {
            try {
                runnable.run();
            }catch (Exception e){
                log.error("execute schedule delay fail,",e);
            }
        },initialDelay,delay,unit);
    };
}
