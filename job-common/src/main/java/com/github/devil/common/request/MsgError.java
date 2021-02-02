package com.github.devil.common.request;

import com.github.devil.common.serialization.JobSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/2/2
 * 客户端发送信息错误
 **/
@Data
@AllArgsConstructor
public class MsgError implements JobSerializable {

    private String errorMsg;

    private JobSerializable jobSerializable;
}
