package com.github.devil.srv.akka;

import akka.actor.AbstractActor;
import com.github.devil.common.request.HeartBeat;
import com.github.devil.srv.akka.request.Echo;
import com.github.devil.srv.akka.request.ServerInfo;

/**
 * @author eric.yao
 * @date 2021/1/20
 **/
public class MainActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Echo.class,this::echo)
                .match(HeartBeat.class,this::onReceiveHeartBeat)
                .build();
    }

    private void echo(Echo echo){
        ServerHolder.onReceive(echo.getCurrentServer(),echo.getSendTime());
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setReceiverTime(System.currentTimeMillis());
        serverInfo.setSurviveCount(MainAkServer.getAllSurvivalWorker().size());
        serverInfo.setServerHost(MainAkServer.getCurrentHost());
        getSender().tell(serverInfo,getSelf());
    }

    //todo
    private void onReceiveHeartBeat(HeartBeat heartBeat){
        System.out.println(heartBeat);
    }
}
