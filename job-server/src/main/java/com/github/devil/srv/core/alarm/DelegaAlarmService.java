package com.github.devil.srv.core.alarm;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author eric.yao
 * @date 2021/2/26
 **/
@Slf4j
public class DelegaAlarmService implements AlarmService{

    private List<AlarmService> alarmServices;

    public DelegaAlarmService(List<AlarmService> alarmServices){
        this.alarmServices = alarmServices;
    }

    @Override
    public void alarm(String message) {
        if (alarmServices != null && !alarmServices.isEmpty()){
            for (AlarmService alarmService : alarmServices) {
                try {
                    alarmService.alarm(message);
                }catch (Exception e){
                    log.error("alarm error,alarm type:{},msg:{},error:",alarmService.getClass().getSimpleName(),message,e);
                }
            }
        }
    }
}
