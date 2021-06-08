package com.github.devil.client.akka;

import akka.pattern.Patterns;
import com.github.devil.client.ThreadUtil;
import com.github.devil.common.dto.HeartBeat;
import com.github.devil.common.dto.ServicesRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.github.devil.common.CommonConstants.WORK_HEART_BEAT;

/**
 * @author eric.yao
 * @date 2021/2/20
 **/
@Slf4j
class ServiceHolder {

    private final static Set<String> SRVS = new HashSet<>();

    private static Long TIME = 0L;

    public synchronized static void receiveSrv(ServicesRes res){

        /**
         * 获取时间小于
         */
        if (TIME <= res.getTime()){
            SRVS.addAll(res.getServices());
            TIME = res.getTime();
        }else {
            log.warn("receive an expired srv res:{}",res);
        }
    }

    public static String getOneSrv(){
        List<String> servers = new ArrayList<>(SRVS);
        if (servers.isEmpty()){
            throw new RuntimeException("un survive server obtain");
        }
        return servers.get(new Random().nextInt(servers.size()));
    }

    private static void sendHeartBeat(String server,String appName){
        try {
            if (log.isDebugEnabled()){
                log.debug("send heartbeat from [{}] to [{}]",appName,server);
            }
            HeartBeat heartBeat = new HeartBeat();
            heartBeat.setAppName(appName);
            heartBeat.setTimeStamp(System.currentTimeMillis());
            heartBeat.setWorkerAddress(ClientAkkaServer.getCurrentHost());
            CompletableFuture<Object> completableFuture = Patterns.ask(ClientAkkaServer.getSrv(server),heartBeat, Duration.ofMillis(1000)).toCompletableFuture();
            ServicesRes servicesRes = (ServicesRes)completableFuture.get(1000, TimeUnit.MILLISECONDS);
            ServiceHolder.receiveSrv(servicesRes);
        }catch (Exception e){
            log.error("cannot send heartbeat message to server:[{}],maybe this server has down",server,e);
        }
    }

    /**
     * 初始化服务心跳
     * @Param servers
     */
    public static void initHeartBeat(List<String> servers){
        Assert.notEmpty(servers,"servers list is required");

        SRVS.addAll(servers);

        ThreadUtil.SCHEDULE.scheduleWithFixedDelay(() -> {
            Set<String> tmp = new HashSet<>(SRVS);
            tmp.forEach(server -> sendHeartBeat(server,ClientAkkaServer.getAppName()));
        },10,WORK_HEART_BEAT, TimeUnit.MILLISECONDS);
    }
}
