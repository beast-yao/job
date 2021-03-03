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
                                                                    newThreadFactory("SCHEDULE"));

    public final static ScheduledExecutorService JOB_PUSH = new ScheduledThreadPoolExecutor(1,
                                                                    newThreadFactory("JOB-PUSH"));

    public final static ThreadPoolExecutor GLOBAL = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                                        Runtime.getRuntime().availableProcessors() * 4,
                                            60,
                                                        TimeUnit.SECONDS,
                                                        new SynchronousQueue<>(),
                                                        newThreadFactory("GLOBAL"));

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

    private static ThreadFactory newThreadFactory(String name) {
        return r -> {
            Thread thread = new Thread(r);
            thread.setName(name);
            thread.setUncaughtExceptionHandler(newHandler());
            return thread;
        };
    }

    private static Thread.UncaughtExceptionHandler newHandler() {
        return (Thread t, Throwable e) -> {
          if (log.isErrorEnabled()) {
              log.error("thread error,thread-name:{},error:", t.getName(), e);
          }
        };
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable,long delay,long rate,TimeUnit unit){
       return SCHEDULE.scheduleAtFixedRate(() -> {
            try {
                runnable.run();
            }catch (Exception e){
                log.error("execute schedule fail,",e);
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
                log.error("execute schedule fail,",e);
            }
        },initialDelay,delay,unit);
    };
}
