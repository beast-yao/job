package com.github.devil.srv.service;

import com.github.devil.srv.dto.response.ServiceDTO;

import java.util.List;

/**
 * @author Yao
 * @date 2021/3/15
 **/
public interface ServerManagerService {

    /**
     * 查询所有服务地址信息
     * @return
     */
    List<ServiceDTO> getServices();

}
