package com.github.devil.common.enums;

import com.github.devil.common.corn.CronSequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.Optional;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Getter
@AllArgsConstructor
public enum TimeType {

    /**
     * 固定频率
     */
    FIX_RATE("固定频率"){
        @Override
        public Date getNext(Date preTriggerTime, String timeValue){
            if (timeValue == null ) {
                return preTriggerTime;
            }else {
                try {
                    Long value = Long.parseLong(timeValue);
                    return new Date(System.currentTimeMillis()+value);
                }catch (Exception e){

                    return preTriggerTime;
                }
            }
        }
    },

    /**
     * 延时
     */
    DELAY("延时"){
        @Override
        public Date getNext(Date preTriggerTime, String timeValue){
            if (timeValue == null ) {
                return null;
            }else {
                try {
                    Long value = Long.parseLong(timeValue);
                    return new Date(System.currentTimeMillis()+value);
                }catch (Exception e){

                    return null;
                }
            }
        }
    },

    /**
     * 固定时间
     */
    FIX_DATE("固定时间"){
        @Override
        public Date getNext(Date preTriggerTime, String timeValue){
            return Optional.ofNullable(preTriggerTime).orElseGet(() -> {
                try {
                    Long value = Long.parseLong(timeValue);
                    return new Date(value);
                }catch (Exception e){

                    return null;
                }
            });
        }
    },

    /**
     * corn 表达式
     */
    CORN("corn 表达式"){
        @Override
        public Date getNext(Date preTriggerTime, String timeValue){
            return new CronSequenceGenerator(timeValue).next(new Date());
        }
    }

    ;


    String message;

    public Date getNext(Date preTriggerTime,String timeValue){
        return new Date();
    }
}
