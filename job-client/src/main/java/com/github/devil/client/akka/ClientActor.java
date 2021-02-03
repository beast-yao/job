package com.github.devil.client.akka;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import com.github.devil.client.spring.TaskCenter;
import com.github.devil.common.request.MsgError;
import com.github.devil.common.request.WorkerExecuteReq;
import lombok.extern.slf4j.Slf4j;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
@Slf4j
public class ClientActor extends AbstractActor {

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(WorkerExecuteReq.class,this::onReceiveReq)
                .match(MsgError.class,this::onError)
                .build()
                ;
    }

    private void onError(MsgError msgError){
        //todo
        log.info("{}",msgError);
    }

    private void onReceiveReq(WorkerExecuteReq executeReq){
        TaskCenter.runProcess(executeReq);
    }
}
