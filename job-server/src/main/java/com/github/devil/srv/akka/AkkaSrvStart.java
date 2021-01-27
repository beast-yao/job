package com.github.devil.srv.akka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author eric.yao
 * @date 2021/1/27
 **/
@Slf4j
@Component
public class AkkaSrvStart implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationStartedEvent) {
       Environment environment =  applicationStartedEvent.getApplicationContext().getEnvironment();
       MainAkServer.start(environment);
    }
}
