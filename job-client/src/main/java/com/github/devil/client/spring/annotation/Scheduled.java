package com.github.devil.client.spring.annotation;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
public @interface Scheduled {

    /**
     * corn
     * @return
     */
    String cron() default "";

    /**
     * fixedDelay
     * @return
     */
    String fixedDelay() default "";

    /**
     * fixedRate
     * @return
     */
    String fixedRate() default "";

    /**
     * uniqueName
     * @return
     */
    String uniqueName();

    /**
     * fixWorker
     * @return
     */
    String fixWorker();
}
