package com.github.devil.timer;

/**
 * @author eric.yao
 * @date 2021/1/13
 **/
public interface TimerTask extends Runnable{

    /**
     * 执行逻辑
     */
    void run();

}
