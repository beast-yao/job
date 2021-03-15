package com.github.devil.srv.dto.response;

import lombok.Data;

import java.util.Map;

/**
 * @author Yao
 * @date 2021/3/15
 **/
@Data
public class ServiceDTO {

    private String host;

    private String status;

    private Map<String,Object> metaData;
}
