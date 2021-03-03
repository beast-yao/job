package com.github.devil.common.dto;

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
public class MsgError<T extends JobSerializable> implements JobSerializable {

    private String errorMsg;

    private T data;
}
