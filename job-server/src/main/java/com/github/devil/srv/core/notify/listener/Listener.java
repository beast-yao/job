package com.github.devil.srv.core.notify.listener;

import com.github.devil.srv.core.notify.event.Event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author eric.yao
 * @date 2021/1/29
 **/
public interface Listener<T extends Event> {

    /**
     * 是否支持当前event
     * @param event
     * @return
     */
    default boolean support(Event event){
        if (event == null){
            return false;
        }
        Type[] types = this.getClass().getGenericInterfaces();
        Optional<Type> optional = Arrays.stream(types).filter(type -> type instanceof ParameterizedType && Objects.equals(((ParameterizedType) type).getRawType().getTypeName(),Listener.class.getName())).findFirst();

        if (optional.isPresent()){
            ParameterizedType parameterizedType = (ParameterizedType) optional.get();
            return Objects.equals(parameterizedType.getActualTypeArguments()[0].getTypeName(),event.getClass().getName());
        }else {
            return false;
        }
    }


    /**
     * 监听event
     * @param event
     */
    public void onEvent(T event);
}
