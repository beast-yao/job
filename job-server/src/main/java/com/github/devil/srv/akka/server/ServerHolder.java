package com.github.devil.srv.akka.server;

import akka.pattern.Patterns;
import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.akka.request.Echo;
import com.github.devil.srv.akka.request.ServerInfo;
import com.github.devil.srv.core.MainThreadUtil;
import com.github.devil.srv.core.notify.NotifyCenter;
import com.github.devil.srv.core.notify.event.ServeUnReceiveEvent;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * @author eric.yao
 * @date 2021/1/29
 **/
@Slf4j
public class ServerHolder {

    private final static long HEART_INTERVAL = 1000L;

    @Getter
    private final static Map<String,Long> SURVIVAL = Maps.newConcurrentMap();

    @Getter
    private final static Set<String> DOWN_SERVE = Sets.newConcurrentHashSet();

    /**
     * all servers
     */
    private final static Set<String> ALL_SERVE = Sets.newConcurrentHashSet();

    public static void echo(String serveHost){

        if (!ALL_SERVE.contains(serveHost)) {
            ALL_SERVE.add(serveHost);
            MainThreadUtil.scheduleAtFixedRate(() -> {
                CompletionStage<Object> askEcho = Patterns.ask(MainAkServer.getSrv(serveHost), new Echo(), Duration.ofMillis(HEART_INTERVAL));
                try {
                    ServerInfo serverInfo = (ServerInfo) askEcho.toCompletableFuture().get(HEART_INTERVAL, TimeUnit.MILLISECONDS);
                    onReceiveServerInfo(serverInfo);
                } catch (Exception e) {
                    DOWN_SERVE.add(serveHost);
                    SURVIVAL.remove(serveHost);
                    NotifyCenter.onEvent(new ServeUnReceiveEvent(serveHost));
                }

            }, 10, HEART_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * receive an echo message from the server
     * need to auto add to the server list if not in
     * and should send echo message to the server
     * @param serve
     * @param time
     */
    public static void onReceive(String serve,long time){
        echo(serve);
    }

    /**
     * receive an echo resp from the server
     * this means this server state is up
     * @param serverInfo
     */
    private static void onReceiveServerInfo(ServerInfo serverInfo){
        if (log.isDebugEnabled()) {
            log.debug("{}", serverInfo);
        }
        // receive self message process state to normal
        if (Objects.equals(serverInfo.getServerHost(),MainAkServer.getCurrentHost())
                && !MainAkServer.isNormal()){
            MainAkServer.stateNormal();
        }

        long last = SURVIVAL.getOrDefault(serverInfo.getServerHost(),0L);
        if (last > serverInfo.getReceiverTime()){
            log.warn("receive an expire serve heartbeat");
            return;
        }
        SURVIVAL.put(serverInfo.getServerHost(),serverInfo.getReceiverTime());
        if (DOWN_SERVE.remove(serverInfo.getServerHost())){
            log.info("re receive echo msg from {}",serverInfo.getServerHost());
        }
    }
}
