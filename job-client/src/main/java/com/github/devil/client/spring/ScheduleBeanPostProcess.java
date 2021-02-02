package com.github.devil.client.spring;

import com.github.devil.client.akka.ClientAkkaServer;
import com.github.devil.client.spring.annotation.Scheduled;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
public class ScheduleBeanPostProcess implements BeanPostProcessor, ApplicationContextAware, SmartInitializingSingleton, EmbeddedValueResolverAware {

    private ApplicationContext context;

    private StringValueResolver embeddedValueResolver;

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {

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
            }

        }
        return bean;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }
}
