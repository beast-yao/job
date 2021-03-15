package com.github.devil.srv.controller;

import com.github.devil.common.CommonConstants;
import com.github.devil.srv.dto.response.Resp;
import com.github.devil.srv.dto.response.ServiceDTO;
import com.github.devil.srv.service.ServerManagerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Yao
 * @date 2021/3/15
 **/
@RestController
@RequestMapping(CommonConstants.BASE_CONTROLLER_PATH+CommonConstants.BASE_SERVICE_CONTROLLER_PATH)
public class DistroController {

    @Resource
    private ServerManagerService serverManagerService;

    @GetMapping
    public Resp<List<ServiceDTO>> getAllService(){
        return new Resp<>(0,serverManagerService.getServices());
    }

}
