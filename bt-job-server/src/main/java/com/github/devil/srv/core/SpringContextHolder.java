package com.github.devil.srv.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author eric.yao
 * @date 2021/1/20
 **/
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static  <T> T getBean(Class<T> tClass){
        return context.getBean(tClass);
    }

    public static  <T> T getBean(String name){
        return (T)context.getBean(name);
    }

    public static String getProperty(String key){
        return context.getEnvironment().getProperty(key);
    }

    public static String getProperty(String key,String defaultValue){
        return context.getEnvironment().getProperty(key,defaultValue);
    }
}
