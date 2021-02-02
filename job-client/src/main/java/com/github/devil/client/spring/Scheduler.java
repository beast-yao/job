package com.github.devil.client.spring;

import com.github.devil.common.enums.TimeType;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
public class Scheduler {

    /**
     * 保存uniqueName和method的关系
     */
    private Map<String, Method> executable = new ConcurrentHashMap<>(64);

    /**
     * method 和 bean
     */
    private Map<Object, List<Method>> beans = new ConcurrentHashMap<>(64);

}
