package com.github.devil.common.util;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * create by Yao 2021/7/5
 **/
public class BeanPropUtils {

    public static  <T> Source<T> from(Supplier<T> supplier){
        return new Source<>(supplier);
    }

    public static class Source<T>{

        private final Supplier<T> supplier;

        private Source(Supplier<T> supplier){
            this.supplier = supplier;
        }

        public void to(Consumer<T> consumer) {
            if (consumer != null){
                Optional.ofNullable(supplier).ifPresent(e -> consumer.accept(e.get()));
            }
        }
    }
}
