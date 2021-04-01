package com.github.devil.srv.akka;

import com.github.devil.srv.core.scheduler.MainJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import javax.annotation.Resource;

/**
 * @author eric.yao
 * @date 2021/1/27
 **/
@Slf4j
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
