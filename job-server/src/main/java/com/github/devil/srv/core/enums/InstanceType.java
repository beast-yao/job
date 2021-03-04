package com.github.devil.srv.core.enums;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/3/4
 **/
@Data
@AllArgsConstructor
public enum InstanceType {

    /**
     * 手动触发
     */
    HAND("手动触发"),

    /**
     * 系统自动触发
     */
    AUTO("系统自动触发"),
    ;
    private String describe;
}
