package com.github.devil.srv.akka;

import javax.annotation.Resource;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import com.github.devil.srv.core.scheduler.MainJobService;

/**
 * @author eric.yao
 * @date 2021/1/27
 **/
public class AkkaSrvStart implements ApplicationListener<ApplicationReadyEvent> {

    private ServerProperties serverProperties;

    @Resource
    private MainJobService mainJobService;

    public AkkaSrvStart(ServerProperties serverProperties){
        this.serverProperties = serverProperties;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
       mainJobService.init(serverProperties);
    }
}
