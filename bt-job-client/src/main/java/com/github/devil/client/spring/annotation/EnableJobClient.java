package com.github.devil.client.spring.annotation;

import com.github.devil.client.spring.JobClientAutoConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

/**
 * @author eric.yao
 * @date 2021/3/2
 **/
@Documented
@Target(ElementType.TYPE)
@ImportAutoConfiguration(JobClientAutoConfig.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableJobClient {
}
