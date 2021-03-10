package com.github.devil.srv.dto.response;

import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/2/18
 **/
@Data
public class Resp<T> {

    private int code;

    private T data;

    private String message;

    public Resp(int code,T data){
        this.code = code;
        this.data = data;
    }

    public Resp(int code,T data,String message){
        this(code,data);
        this.message = message;
    }
}
