package com.github.devil.client.akka;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import com.github.devil.client.ThreadUtil;
import com.github.devil.client.process.TaskContext;
import com.github.devil.client.spring.TaskCenter;
import com.github.devil.common.dto.*;
import com.github.devil.common.enums.ResultEnums;
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
                .match(ServicesRes.class,ServiceHolder::receiveSrv)
                .match(MsgError.class,this::onError)
                .build()
                ;
    }

    private void onError(MsgError<?> msgError){
        log.error("receive an error msg info{}",msgError);
    }

    private void onReceiveReq(WorkerExecuteReq executeReq){

        TaskContext taskContext = new TaskContext();
        taskContext.setInstanceId(executeReq.getInstanceId());
        taskContext.setJobId(executeReq.getJobId());
        taskContext.setParam(executeReq.getParams());
        taskContext.setWorkInstanceId(executeReq.getWorkInstanceId());
        taskContext.setServer(executeReq.getServerHost());
        taskContext.setName(executeReq.getUniqueName());
        taskContext.setTaskType(executeReq.getTaskType());
        taskContext.setMeatInfo(executeReq.getJobMeta());

        TaskCenter.beforeProcess(taskContext);

        ThreadUtil.GLOBAL.execute(() -> {
            try {
                ResultEnums result =  TaskCenter.runProcess(taskContext);

                WorkerExecuteRes res = new WorkerExecuteRes();
                res.setInstanceId(executeReq.getInstanceId());
                res.setJobId(executeReq.getJobId());
                res.setResult(result);
                res.setWorkInstanceId(executeReq.getWorkInstanceId());
                //上报执行结果
                ClientAkkaServer.getSrv().tell(res,getSelf());
            }catch (Exception e){
                log.error("execute task fail,",e);
            }
        });
        getSender().tell(new NoResponse(),getSelf());
    }
}
