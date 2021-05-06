package com.github.devil.srv.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
@Getter
@AllArgsConstructor
public enum JobStatus {

    /**
     * 正常
     */
    NORMAL("正常"),

    /**
     * 停止
     */
    STOP("停止"),


    /**
     * 完成
     */
    COMPLETE("完成"),

    ;

    String message;
}
