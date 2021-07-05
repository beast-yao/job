package com.github.devil.client.akka;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import com.github.devil.client.ThreadUtil;
import com.github.devil.client.exception.RejectException;
import com.github.devil.client.process.TaskContext;
import com.github.devil.client.spring.TaskCenter;
import com.github.devil.common.dto.*;
import com.github.devil.common.enums.ResultEnums;
import com.github.devil.common.util.BeanPropUtils;
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
        TaskContext taskContext = getContent(executeReq);

        try {
            this.beforeTask(taskContext);
            this.run(taskContext);
        }catch (RejectException rejectException){
            taskContext.getLogger().error("execute cancel ,task [{}], instance [{}], reject reason [{}]",
                taskContext.getInstanceId(),taskContext.getWorkInstanceId(),rejectException.getMessage());

            if (log.isWarnEnabled()) {
                log.warn("execute task [{}] cancel,message [{}] ", taskContext.getName(), rejectException.getMessage());
            }
        }finally {
            getSender().tell(new NoResponse(),getSelf());
        }
    }

    private TaskContext getContent(WorkerExecuteReq executeReq){
        TaskContext taskContext = new TaskContext();
        BeanPropUtils.from(executeReq::getInstanceId).to(taskContext::setInstanceId);
        BeanPropUtils.from(executeReq::getJobId).to(taskContext::setJobId);
        BeanPropUtils.from(executeReq::getParams).to(taskContext::setParam);
        BeanPropUtils.from(executeReq::getWorkInstanceId).to(taskContext::setWorkInstanceId);
        BeanPropUtils.from(executeReq::getServerHost).to(taskContext::setServer);
        BeanPropUtils.from(executeReq::getUniqueName).to(taskContext::setName);
        BeanPropUtils.from(executeReq::getTaskType).to(taskContext::setTaskType);
        BeanPropUtils.from(executeReq::getJobMeta).to(taskContext::setMeatInfo);
        return taskContext;
    }

    private void beforeTask(TaskContext taskContext) throws RejectException {
        TaskCenter.beforeProcess(taskContext);
    }

    private void run(TaskContext taskContext) {
        ThreadUtil.GLOBAL.execute(() -> {
            try {
                ResultEnums result =  TaskCenter.runProcess(taskContext);

                WorkerExecuteRes res = new WorkerExecuteRes();
                BeanPropUtils.from(taskContext::getInstanceId).to(res::setInstanceId);
                BeanPropUtils.from(taskContext::getJobId).to(res::setJobId);
                BeanPropUtils.from(taskContext::getWorkInstanceId).to(res::setWorkInstanceId);
                BeanPropUtils.from(() -> result).to(res::setResult);
                //上报执行结果
                ClientAkkaServer.getSrv().tell(res,getSelf());
            }catch (Exception e){
                log.error("execute task fail,",e);
            }
        });
    }
}
