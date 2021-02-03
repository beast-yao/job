package com.github.devil.client.spring;

import com.github.devil.client.process.InvokeProcess;
import com.github.devil.client.process.TaskContext;
import com.github.devil.common.enums.ResultEnums;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author eric.yao
 * @date 2021/2/3
 **/
@Data
@AllArgsConstructor
public class MethodInvokeProcess implements InvokeProcess {

    private Object bean;

    private Method method;

    @Override
    public ResultEnums run(TaskContext taskContext) {
        return null;
    }
}
