package com.github.devil.srv.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.devil.common.enums.BaseEnums;

import java.io.IOException;

/**
 * create by Yao 2021/3/31
 **/
public class BaseEnumSerial extends JsonSerializer<BaseEnums> {
    @Override
    public void serialize(BaseEnums baseEnums, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (baseEnums == null) {
            jsonGenerator.writeNull();
        }else {
            jsonGenerator.writeString(baseEnums.getMessage());
        }
    }
}
