package com.github.devil.client.akka;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.Patterns;
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
class ClientActor extends AbstractActor {

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(WorkerExecuteReq.class,this::onReceiveReq)
                .match(MsgError.class,this::onError)
                .build()
                ;
    }

    private void onError(MsgError<?> msgError){
        log.error("receive an error msg info{}",msgError);
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
