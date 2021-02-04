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
public class AkkaSrvStart implements ApplicationListener<ApplicationReadyEvent> {

    private ServerProperties serverProperties;

    public AkkaSrvStart(ServerProperties serverProperties){
        this.serverProperties = serverProperties;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
       Environment environment =  applicationReadyEvent.getApplicationContext().getEnvironment();
       MainAkServer.start(serverProperties);
    }
}
