package com.github.devil.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
public class TimeUtils {

    public static LocalDateTime convertDate(Date date){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
    }
}
