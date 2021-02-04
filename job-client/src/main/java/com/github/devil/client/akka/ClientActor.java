package com.github.devil.client.akka;

import akka.actor.AbstractActor;
import akka.actor.DeadLetter;
import akka.actor.ReceiveTimeout;
import akka.japi.pf.ReceiveBuilder;
import com.github.devil.client.spring.TaskCenter;
import com.github.devil.common.enums.ResultEnums;
import com.github.devil.common.request.MsgError;
import com.github.devil.common.request.WorkerExecuteReq;
import com.github.devil.common.request.WorkerExecuteRes;
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
                .match(DeadLetter.class,this::onDead)
                .match(ReceiveTimeout.class,this::onTimeOut)
                .build()
                ;
    }

    private void onError(MsgError msgError){
        //todo
        log.info("{}",msgError);
    }

    /**
     * 监听死信
     * @param deadLetter
     */
    private void onDead(DeadLetter deadLetter){
        log.error("receive an deadLetter,send msg fail：{}",deadLetter.message());
    }
    private void onTimeOut(ReceiveTimeout timeout){
        log.error("receive an deadLetter,send msg fail：{}",timeout);
    }

    private void onReceiveReq(WorkerExecuteReq executeReq){
        ResultEnums result =  TaskCenter.runProcess(executeReq);

        WorkerExecuteRes res = new WorkerExecuteRes();
        res.setInstanceId(executeReq.getInstanceId());
        res.setJobId(executeReq.getJobId());
        res.setResult(result);
        res.setWorkInstanceId(executeReq.getWorkInstanceId());
        //上报执行结果
        getSender().tell(res,getSelf());
    }
}
