package com.github.devil.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Getter
@AllArgsConstructor
public enum  ExecuteType {

    /**
     * 单机
     */
    SINGLE("单机"),

    /**
     * 广播执行
     */
    BROADCAST("单机");

    String message;
}
