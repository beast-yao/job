package com.github.devil.srv.akka;

import akka.actor.*;
import akka.event.Logging;
import akka.routing.RoundRobinPool;
import static com.github.devil.common.CommonConstants.*;

import com.github.devil.common.util.InetUtils;
import com.github.devil.srv.akka.server.ServerHolder;
import com.github.devil.srv.akka.ha.ServerManager;
import com.github.devil.srv.core.SpringContextHolder;
import com.github.devil.srv.core.exception.JobException;
import com.github.devil.srv.core.notify.NotifyCenter;
import com.github.devil.srv.core.notify.listener.ServeUnReceiveListener;
import com.github.devil.srv.core.notify.listener.WorkerDownListener;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author eric.yao
 * @date 2021/1/20
 **/
@Slf4j
public class MainAkServer {

    private final static String AKKA_CONF = "akka.conf";

    private static  ActorSystem system;

    @Getter
    private static ActorRef actorRef;

    @Getter
    private static String currentHost;

    public static void start(@Nonnull ServerProperties serverProperties){
        //初始化akka server
        initServer(serverProperties);

        //初始化节点通信
        initEcho(serverProperties);

        //初始化监听器
        initListener();
    }

    private static void initServer(ServerProperties serverProperties){
        log.info("===============Job SRV Starting============");
        Map<String,String> configMap = Maps.newHashMap();
        String address = getAddress(serverProperties);
        if (address != null){
            configMap.put("akka.remote.artery.canonical.hostname",address);
        }else {
            throw new JobException("cannot auto find local Ip address,Please set it");
        }

        String port = String.valueOf(serverProperties.getPort());

        configMap.put("akka.remote.artery.canonical.port",port);

        log.info("Start Job SRV,Host:{},Port:{}",address,port);

        currentHost = address+":"+port;

        Config config = ConfigFactory.parseMap(configMap).withFallback(ConfigFactory.load(AKKA_CONF));

        system = ActorSystem.apply(MAIN_JOB_SRV_NAME,config);

        actorRef = system.actorOf(Props.create(MainActor.class)
                .withDispatcher("akka.job-srv-dispatcher")
                .withRouter(new RoundRobinPool(Runtime.getRuntime().availableProcessors())), MAIN_JOB_ACTOR_PATH);

        system.eventStream().subscribe(actorRef, DeadLetter.class);

        log.info("===============Job SRV Started==============");
    }

    private static void initEcho(ServerProperties serverProperties){
        List<String> memberList = Optional.ofNullable(serverProperties.getMemberList()).orElseGet(ArrayList::new);

        Set<String> servers = Sets.newHashSet(memberList);

        servers.add(currentHost);

        //每个节点之间进行通信，确保节点存活
        servers.forEach(ServerHolder::echo);
    }

    private static void initListener(){
        //注册监听器
        NotifyCenter.addListener(new ServeUnReceiveListener());
        NotifyCenter.addListener(new WorkerDownListener());
    }

    public static ActorSelection getSrv(String host){
        return system.actorSelection(String.format(AKKA_SRV_PATH,MAIN_JOB_SRV_NAME,host,MAIN_JOB_ACTOR_PATH));
    }

    public static ActorSelection getWorker(String host){
        return system.actorSelection(String.format(AKKA_SRV_PATH,MAIN_JOB_WORKER_NAME,host,MAIN_JOB_WORKER_ACTOR_PATH));
    }

    public static String nextHealthServer(){
        return SpringContextHolder.getBean(ServerManager.class).getNextServer();
    }

    public static Set<String> getAllSurvivalServer(){
        return ServerHolder.getSURVIVAL().keySet();
    }

    private static String getAddress(ServerProperties serverProperties){
        String address = serverProperties.getHost();
        if (address == null || address.isEmpty() ){
            InetUtils.HostInfo hostInfo = InetUtils.findFirstNonLoopbackHostInfo();
            if (hostInfo != null){
                return hostInfo.getIpAddress();
            }
            return null;
        }
        return address;
    }

}
