package com.github.devil.sample.schedule;

import com.github.devil.client.spring.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author eric.yao
 * @date 2021/2/3
 **/
@Component
public class MyScheduleTask {

    @Scheduled(uniqueName = "test1")
    public void test(){

    }

}
