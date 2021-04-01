package com.github.devil.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Getter
@AllArgsConstructor
public enum  ExecuteType implements BaseEnums{

    /**
     * 单机
     */
    SINGLE("单机执行"),

    /**
     * 广播执行
     */
    BROADCAST("广播执行");

    String message;

}
