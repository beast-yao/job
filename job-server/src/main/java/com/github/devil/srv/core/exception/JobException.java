package com.github.devil.srv.core.exception;

import lombok.Getter;

/**
 * @author eric.yao
 * @date 2021/1/19
 **/
@Getter
public class JobException extends RuntimeException {

    private String message;

    public JobException(String message){
        super(message);
        this.message = message;
    }

    public JobException(String message,Throwable throwable){
        super(message,throwable);
        this.message = message;
    }

}
