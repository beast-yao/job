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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author eric.yao
 * @date 2021/1/29
 **/
@Slf4j
public class ServerHolder {

    private final static long HEART_INTERVAL = 1000L;

    @Getter
    private final static Map<String,ServerInfo> SURVIVAL = Maps.newConcurrentMap();

    @Getter
    private final static  Map<String,ServerInfo> DOWN_SERVE = Maps.newConcurrentMap();

    /**
     * all servers
     */
    private final static Set<String> ALL_SERVE = Sets.newConcurrentHashSet();

    public static void echo(String serveHost){

        if (ALL_SERVE.add(serveHost)) {
            MainThreadUtil.scheduleAtFixedRate(() -> {
                try {
                    ServerInfo serverInfo = ask(serveHost);
                    onReceiveServerInfo(serverInfo);
                } catch (Exception e) {
                    DOWN_SERVE.put(serveHost,SURVIVAL.remove(serveHost));
                    NotifyCenter.onEvent(new ServeUnReceiveEvent(serveHost));
                }

            }, 10, HEART_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    public static ServerInfo ask(String serverHost) throws InterruptedException, ExecutionException, TimeoutException {
        CompletionStage<Object> askEcho = Patterns.ask(MainAkServer.getSrv(serverHost), new Echo(), Duration.ofMillis(HEART_INTERVAL));
        return  (ServerInfo) askEcho.toCompletableFuture().get(HEART_INTERVAL, TimeUnit.MILLISECONDS);
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

        ServerInfo last = SURVIVAL.get(serverInfo.getServerHost());
        long lastTime = last == null? 0L : last.getReceiverTime();
        if (lastTime > serverInfo.getReceiverTime()){
            log.warn("receive an expire serve heartbeat");
            return;
        }
        SURVIVAL.put(serverInfo.getServerHost(),serverInfo);
        if (DOWN_SERVE.remove(serverInfo.getServerHost()) != null){
            log.info("re receive echo msg from {}",serverInfo.getServerHost());
        }
    }
}
