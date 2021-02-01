package com.github.devil.srv.akka.worker;

import com.github.devil.common.CommonConstants;
import com.github.devil.srv.core.MainThreadUtil;
import com.github.devil.srv.core.notify.NotifyCenter;
import com.github.devil.srv.core.notify.event.WorkerDownEvent;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author eric.yao
 * @date 2021/2/1
 **/
@Slf4j
public class Instance {

    private String appName;

    private Map<String,Long> survival = Maps.newConcurrentMap();

    private Set<String> down = Sets.newConcurrentHashSet();

    public Instance(String appName){
        this.appName = appName;
        MainThreadUtil.SCHEDULE.scheduleWithFixedDelay(() -> {
            Iterator<Map.Entry<String,Long>> iterator = survival.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String,Long> entry = iterator.next();
                if (entry.getValue() <= System.currentTimeMillis() - CommonConstants.WORK_HEART_BEAT*3){
                    down.add(entry.getKey());
                    iterator.remove();

                    WorkerDownEvent event = new WorkerDownEvent();
                    event.setAppName(this.appName);
                    event.setWorkHost(entry.getKey());
                    NotifyCenter.onEvent(event);
                }
            }
        },10,CommonConstants.WORK_HEART_BEAT, TimeUnit.MILLISECONDS);
    }

    void onHeart(String workHost,Long timStamp){
        long last = survival.getOrDefault(workHost,0L);
        if (timStamp < last){
            log.warn("receive an expire work heartbeat");
            return;
        }
        survival.put(workHost, timStamp);
        if (down.remove(workHost)){
            log.info("re receive heartBeat from {}",workHost);
        }
    }

    Set<String> getSurvivalWorkers(){
        return survival.keySet();
    }
}
