package com.github.devil.srv.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.devil.srv.akka.request.ServerInfo;
import com.github.devil.srv.akka.server.ServerHolder;
import com.github.devil.srv.core.Constants;
import com.github.devil.srv.dto.response.ServiceDTO;
import com.github.devil.srv.service.ServerManagerService;

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
        Map<String,Object> metaData = new HashMap<>(8);

        long time = (Long) e.getMetaData().getOrDefault(Constants.META_TIME,0L);
        if (time > 0){
            metaData.put(Constants.META_TIME, LocalDateTime.now().minusNanos(System.nanoTime() - time)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        dto.setMetaData(metaData);
        dto.setStatus(status);
        return dto;
    }
}
