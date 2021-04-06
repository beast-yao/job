package com.github.devil.common.enums;

import com.github.devil.common.corn.CronSequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Getter
@AllArgsConstructor
public enum TimeType implements BaseEnums{

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
                    return new Date(Optional.ofNullable(preTriggerTime).orElseGet(Date::new).getTime()+value);
                }catch (Exception e){

                    return preTriggerTime;
                }
            }
        }

        @Override
        public boolean validExpression(String timeValue) {
            boolean valid = super.validExpression(timeValue);
            if (valid){
                try {
                    return Long.parseLong(timeValue.trim()) >= 1000;
                }catch (Exception e){
                    return false;
                }
            }
            return false;
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

        @Override
        public boolean validExpression(String timeValue) {
            return FIX_RATE.validExpression(timeValue);
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

        @Override
        public boolean validExpression(String timeValue) {
            boolean valid = super.validExpression(timeValue);
            if (!valid){
                return false;
            }
            try {
               return new Date(Long.parseLong(timeValue)).after(new Date());
            }catch (Exception e){
                try {
                   return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").parse(timeValue).after(new Date());
                } catch (Exception parseException) {
                    return false;
                }
            }
        }
    },

    /**
     * corn 表达式
     */
    CORN("corn 表达式"){
        @Override
        public Date getNext(Date preTriggerTime, String timeValue){
            return new CronSequenceGenerator(timeValue).next(Optional.ofNullable(preTriggerTime).orElseGet(Date::new));
        }

        @Override
        public boolean validExpression(String timeValue) {
            boolean valid = super.validExpression(timeValue);
            if (!valid){
                return false;
            }
            try {
                new CronSequenceGenerator(timeValue);
                return true;
            }catch (Exception e){
                return false;
            }
        }
    }

    ;


    String message;

    public Date getNext(Date preTriggerTime,String timeValue){
        return new Date();
    }

    public boolean validExpression(String timeValue){
        if (timeValue == null || timeValue.isEmpty()){
            return false;
        }
        return true;
    }
}
