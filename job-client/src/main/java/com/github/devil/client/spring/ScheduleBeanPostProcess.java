package com.github.devil.client.spring;

import com.github.devil.client.akka.ClientAkkaServer;
import com.github.devil.client.spring.annotation.Scheduled;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.TaskScheduler;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
public class ScheduleBeanPostProcess implements BeanPostProcessor, SmartInitializingSingleton {


    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    @Override
    public void afterSingletonsInstantiated() {
        //todo push task to server
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof AopInfrastructureBean || bean instanceof TaskScheduler ||
                bean instanceof ScheduledExecutorService ||
                bean instanceof ClientAkkaServer) {
            // Ignore AOP infrastructure such as scoped proxies.
            return bean;
        }

        // 代理类需要找到原始类型
        Class target = AopUtils.getTargetClass(bean);
        if (!nonAnnotatedClasses.contains(target)) {
            Map<Method, Scheduled> annotations = MethodIntrospector.selectMethods(target, (MethodIntrospector.MetadataLookup<Scheduled>) method -> AnnotationUtils.findAnnotation(method, Scheduled.class));

            if (annotations.isEmpty()){
                nonAnnotatedClasses.add(target);
            }else {
                annotations.forEach((method, scheduled) -> registerTask(bean,method,scheduled));
            }
        }
        return bean;
    }


    private void registerTask(Object bean,Method method,Scheduled scheduled){
        String uniqueName = scheduled.uniqueName();
        if (uniqueName.isEmpty()){
            uniqueName = defaultName(bean,method);
        }
        TaskCenter.registerProcess(uniqueName,new MethodInvokeProcess(bean, method));
    }

    private String defaultName(Object bean,Method method){
        String params = Arrays.stream(method.getParameterTypes()).map(Class::getName).collect(Collectors.joining(","));
        return ClientAkkaServer.getAppName()+"#"+bean.getClass().getName()+"#"+method.getName()+"("+params+")";
    }
}
