package com.github.devil.timer;

/**
 * @author eric.yao
 * @date 2021/1/13
 **/
public interface TimerFuture {

    /**
     * 获取任务
     * @return
     */
    TimerTask getTask();

    /**
     * 取消任务
     */
    void cancel();

    /**
     * 判断是否取消
     * @return
     */
    boolean isCanceled();

    /**
     * 判断是否结束
     * @return
     */
    boolean isDone();

}
