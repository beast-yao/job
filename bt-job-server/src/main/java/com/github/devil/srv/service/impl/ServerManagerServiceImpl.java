package com.github.devil.srv.service.impl;

import com.github.devil.srv.akka.request.ServerInfo;
import com.github.devil.srv.akka.server.ServerHolder;
import com.github.devil.srv.dto.response.ServiceDTO;
import com.github.devil.srv.service.ServerManagerService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Yao
 * @date 2021/3/15
 **/
@Service
public class ServerManagerServiceImpl implements ServerManagerService {

    @Override
    public List<ServiceDTO> getServices() {
        Map<String, ServerInfo> survival = ServerHolder.getSURVIVAL();
        Map<String, ServerInfo> down = ServerHolder.getDOWN_SERVE();

        List<ServiceDTO> res = survival.values().stream()
                .map(e -> convert("UP",e)).collect(Collectors.toList());

        res.addAll(down.values().stream()
                .map(e -> convert("DOWN",e)).collect(Collectors.toList()));

        res.sort(Comparator.comparing(ServiceDTO::getHost));
        return res;
    }

    private ServiceDTO convert(String status,ServerInfo e){
        ServiceDTO dto = new ServiceDTO();
        dto.setHost(e.getServerHost());
        dto.setMetaData(e.getMetaData());
        dto.setStatus(status);
        return dto;
    }
}