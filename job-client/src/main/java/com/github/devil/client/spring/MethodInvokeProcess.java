package com.github.devil.client.spring;

import com.github.devil.client.process.InvokeProcess;
import com.github.devil.client.process.TaskContext;
import com.github.devil.common.enums.ResultEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author eric.yao
 * @date 2021/2/3
 **/
@Data
@Slf4j
@AllArgsConstructor
public class MethodInvokeProcess implements InvokeProcess {

    private Object bean;

    private Method method;

    @Override
    public ResultEnums run(TaskContext taskContext) {

        Object[] params = getParams(taskContext,method);
        try {
            method.invoke(bean,params);
        } catch (Exception e) {
            log.error("execute an job error,",e);
            taskContext.getLogger().error("execute method invoke error,uniqueName:{}",taskContext.getName(),e);
        }
        return null;
    }

    private Object[] getParams(TaskContext taskContext,Method method){
        Class[] clazs = method.getParameterTypes();
        Object[] params = new Object[clazs.length];
        for (int i = 0; i < clazs.length; i++) {
            params[i] = null;
            Class type = clazs[i];
            if (type.isAssignableFrom(String.class)){
                params[i] = taskContext.getParam();
            }else if (type.isAssignableFrom(TaskContext.class)){
                params[i] = taskContext;
            }
        }
        return params;
    }

}
