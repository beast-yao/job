package com.github.devil.client.akka;

import akka.actor.*;
import akka.routing.RoundRobinPool;
import com.github.devil.common.util.InetUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.devil.common.CommonConstants.*;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
@Slf4j
@AllArgsConstructor
public class ClientAkkaServer {

    private AkkaProperties akkaProperties;

    private final static AtomicBoolean isStart = new AtomicBoolean(false);

    private final static String AKKA_CONF = "META-INF/akka-client.conf";

    private static ActorSystem system;

    @Getter
    private static ActorRef actorRef;

    @Getter
    private static String currentHost;

    @Getter
    private static String appName;

    public static boolean hasStart(){
        return isStart.get();
    }

    public void init(){

        if (isStart.get()){
            return;
        }

        if (isStart.compareAndSet(false,true)) {
            Objects.requireNonNull(akkaProperties.getAppName(), "client appName is required");

            ClientAkkaServer.appName = akkaProperties.getAppName();

            //初始化akka srv
            initSrv(akkaProperties);

            //初始化服务心跳
            ServiceHolder.initHeartBeat(akkaProperties.getServers());

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                isStart.set(false);
                system.terminate();
            }));
        }
    }

    /**
     * 初始化akka服务
     * @param akkaProperties
     */
    private void initSrv(AkkaProperties akkaProperties){
        log.info("===============Job Client Starting============");
        Map<String,String> configMap = new HashMap<>();
        String address = getAddress(akkaProperties);
        if (address != null){
            configMap.put("akka.remote.artery.canonical.hostname",address);
        }else {
            throw new IllegalArgumentException("cannot auto find local Ip address,Please set it");
        }

        Integer port = akkaProperties.getPort();

        configMap.put("akka.remote.artery.canonical.port",String.valueOf(port));

        log.info("Start Job Client,Host:{},Port:{}",address,port);

        currentHost = address+":"+port;

        Config config = ConfigFactory.parseMap(configMap).withFallback(ConfigFactory.load(AKKA_CONF));

        system = ActorSystem.apply(MAIN_JOB_WORKER_NAME,config);

        actorRef = system.actorOf(Props.create(ClientActor.class)
                .withDispatcher("akka.job-client-dispatcher")
                .withRouter(new RoundRobinPool(Runtime.getRuntime().availableProcessors())), MAIN_JOB_WORKER_ACTOR_PATH);

        system.actorOf(Props.create(MessageDeadActor.class));

        log.info("===============Job Client Started==============");
    }

    private static String getAddress(AkkaProperties akkaProperties){
        String address = akkaProperties.getHost();
        if (address == null || address.isEmpty() ){
            InetUtils.HostInfo hostInfo = InetUtils.findFirstNonLoopbackHostInfo();
            if (hostInfo != null){
                return hostInfo.getIpAddress();
            }
            return null;
        }
        return address;
    }

    public static ActorSelection getSrv(){
        return system.actorSelection(String.format(AKKA_SRV_PATH,MAIN_JOB_SRV_NAME,ServiceHolder.getOneSrv(),MAIN_JOB_ACTOR_PATH));
    }

    public static ActorSelection getSrv(String server){
        return system.actorSelection(String.format(AKKA_SRV_PATH,MAIN_JOB_SRV_NAME,server,MAIN_JOB_ACTOR_PATH));
    }
}
