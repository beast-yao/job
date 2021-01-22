package com.github.devil.srv.akka;

import akka.actor.AbstractActor;
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
                .match(ServerInfo.class,this::onReceiveServerInfo)
                .build();
    }

    private void echo(Echo echo){
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setReceiverTime(System.currentTimeMillis());
        //todo holder srv info
        serverInfo.setSurviveCount(0);
        getSender().tell(serverInfo,getSelf());
    }

    //todo
    private void onReceiveServerInfo(ServerInfo serverInfo){
        System.out.println(serverInfo);
    }

}
