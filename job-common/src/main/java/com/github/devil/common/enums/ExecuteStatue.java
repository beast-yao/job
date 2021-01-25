package com.github.devil.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Getter
@AllArgsConstructor
public enum ExecuteStatue {

    /**
     * 等待触发
     */
    WAIT("等待触发"),

    /**
     * 取消
     */
    CANCEL("取消"),

    /**
     * 执行中
     */
    EXECUTING("执行中"),
    /**
     * 执行成功
     */
    SUCCESS("执行成功"),

    /**
     * 执行失败
     */
    FAILURE("执行失败")

    ;


    String message;
}
