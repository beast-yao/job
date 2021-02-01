package com.github.devil.srv.akka.worker;

import com.github.devil.common.request.HeartBeat;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author eric.yao
 * @date 2021/2/1
 **/
public class WorkerHolder {

    private static Map<String,Instance> works = Maps.newConcurrentMap();

    public static void onHeartBeat(HeartBeat heartBeat){
        Instance instance = works.getOrDefault(heartBeat.getAppName(),new Instance(heartBeat.getAppName()));
        instance.onHeart(heartBeat.getWorkerAddress(),heartBeat.getTimeStamp());
    }

    public static Set<String> getSurvivalWorkers(String appName){
        Objects.requireNonNull(appName);
        Instance instance = works.get(appName);
        if (instance == null){
            return Sets.newHashSet();
        }
        return instance.getSurvivalWorkers();
    }

}
