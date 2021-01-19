package com.github.devil.timer;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author eric.yao
 * @date 2021/1/13
 **/
public interface Timer {

    /**
     * 增加一个延时任务
     * @param task
     * @param delay
     * @param unit
     */
    public TimerFuture delay(TimerTask task, long delay, TimeUnit unit);

    /**
     * 停止执行调度
     * @return 返回未处理的任务信息
     */
    public Set<TimerFuture> stop();
}
