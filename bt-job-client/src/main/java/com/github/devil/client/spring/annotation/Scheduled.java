package com.github.devil.client.spring.annotation;

import java.lang.annotation.*;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Scheduled {

    /**
     * should be an unique task name
     * @return taskName
     */
    String taskName();

}
