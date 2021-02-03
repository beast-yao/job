package com.github.devil.srv.akka;

import akka.actor.AbstractActor;
import com.github.devil.common.request.HeartBeat;
import com.github.devil.common.request.LogContent;
import com.github.devil.common.request.LoggingRequest;
import com.github.devil.common.request.MsgError;
import com.github.devil.srv.akka.request.Echo;
import com.github.devil.srv.akka.request.ServerInfo;
import com.github.devil.srv.akka.server.ServerHolder;
import com.github.devil.srv.akka.worker.WorkerHolder;
import com.github.devil.srv.core.SpringContextHolder;
import com.github.devil.srv.core.service.LoggingService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author eric.yao
 * @date 2021/1/20
 **/
@Slf4j
public class MainActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Echo.class,this::echo)
                .match(HeartBeat.class,this::onReceiveHeartBeat)
                .match(LoggingRequest.class,this::onReceiveLog)
                .build();
    }

    private void echo(Echo echo){
        ServerHolder.onReceive(echo.getCurrentServer(),echo.getSendTime());
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setReceiverTime(System.currentTimeMillis());
        serverInfo.setSurviveCount(MainAkServer.getAllSurvivalServer().size());
        serverInfo.setServerHost(MainAkServer.getCurrentHost());
        getSender().tell(serverInfo,getSelf());
    }

    private void onReceiveHeartBeat(HeartBeat heartBeat){
        try {
            WorkerHolder.onHeartBeat(heartBeat);
        }catch (IllegalArgumentException e){
            if (log.isErrorEnabled()){
                log.error("heartbeat error,",e);
            }
            getSender().tell(new MsgError(e.getMessage(),heartBeat),getSelf());
        }
    }

    private void onReceiveLog(LoggingRequest request){
        try {
            SpringContextHolder.getBean(LoggingService.class).saveLogRequest(request);
        }catch (IllegalArgumentException e){
            if (log.isErrorEnabled()){
                log.error("heartbeat error,",e);
            }
            getSender().tell(new MsgError(e.getMessage(),request),getSelf());
        }
    }
}
