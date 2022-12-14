package com.github.devil.srv.config;

import java.util.List;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.devil.srv.dto.response.Resp;

import lombok.extern.slf4j.Slf4j;

/**
 * @author eric.yao
 * @date 2021/2/18
 **/
@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = TypeMismatchException.class)
    public Resp<String> handleMethodError(TypeMismatchException exception){
        log.error("params error,",exception);
        return new Resp<>(HttpStatus.BAD_REQUEST.value(),null,exception.getMessage());
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Resp<String> handleValid(MethodArgumentNotValidException exception){
        log.error("params error,",exception);
        List<ObjectError> errors = exception.getBindingResult().getAllErrors();
        return new Resp<>(HttpStatus.BAD_REQUEST.value(),null,errors.stream().findFirst().map(ObjectError::getDefaultMessage).orElse(""));
    }


    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ServletRequestBindingException.class)
    public Resp<String> handleValid(ServletRequestBindingException exception){
        log.error("params error,",exception);
        return new Resp<>(HttpStatus.BAD_REQUEST.value(),null,exception.getMessage());
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Resp<String> handleValid(IllegalArgumentException exception){
        log.error("params error,",exception);
        return new Resp<>(HttpStatus.BAD_REQUEST.value(),null,exception.getMessage());
    }


    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public Resp<String> handleValid(Exception exception){
        log.error("server error,",exception);
        return new Resp<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),null,"服务异常");
    }

}
