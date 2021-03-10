package com.github.devil.srv.core;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;

/**
 * @author eric.yao
 * @date 2021/3/8
 **/
@Slf4j
public class NamedThreadFactory implements ThreadFactory {

    private String namePrefix;

    public NamedThreadFactory(String namePrefix){
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(namePrefix+"-"+thread.getId());
        thread.setUncaughtExceptionHandler(newHandler());
        return thread;
    }

    private static Thread.UncaughtExceptionHandler newHandler() {
        return (Thread t, Throwable e) -> {
            if (log.isErrorEnabled()) {
                log.error("thread error,thread-name:{},error:", t.getName(), e);
            }
        };
    }

}
