package com.github.devil.srv.akka.ha;

import com.github.devil.srv.akka.server.ServerHolder;
import com.github.devil.srv.akka.worker.WorkerHolder;
import org.springframework.stereotype.Component;

/**
 * @author eric.yao
 * @date 2021/2/1
 **/
@Component
public class ServerManager {

    private ServerChoose serverChoose;

    private ServerChoose workerChoose;

    public ServerManager(ServerChoose serverChoose,
                         ServerChoose workerChoose){
        this.serverChoose = serverChoose;
        this.workerChoose = workerChoose;
    }

    public String getNextServer(){
        return serverChoose.choose(ServerHolder.getSURVIVAL().keySet());
    }

    public String getNextWorker(String appName){
        return workerChoose.choose(WorkerHolder.getSurvivalWorkers(appName));
    }

}
