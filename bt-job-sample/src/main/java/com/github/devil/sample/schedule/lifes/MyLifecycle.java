package com.github.devil.sample.schedule.lifes;

import com.github.devil.client.process.TaskContext;
import com.github.devil.client.process.TaskLifecycle;
import com.github.devil.common.enums.ResultEnums;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author Yao
 * @date 2021/3/16
 **/
@Component
public class MyLifecycle implements TaskLifecycle {

    @Override
    public List<String> name() {
        return Collections.singletonList("test1");
    }

    @Override
    public void beforeTask(TaskContext taskContext) {
        System.out.println(1);
    }

    @Override
    public void afterTask(TaskContext taskContext, ResultEnums enums, Throwable throwable) {
        System.out.println(2);
    }
}
