package com.github.devil.srv.core.alarm;

import lombok.Builder;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/3/1
 **/
@Data
@Builder
public class Message {

    private String title;

    private String content;
}
