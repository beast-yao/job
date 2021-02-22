package com.github.devil.client.akka;

import com.github.devil.common.request.ServicesRes;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author eric.yao
 * @date 2021/2/20
 **/
@Slf4j
class ServiceHolder {

    private final static Set<String> SRVS = new HashSet<>();

    private static Long TIME = 0L;

    public synchronized static void receiveSrv(ServicesRes res){

        /**
         * 获取时间小于
         */
        if (TIME <= res.getTime()){
            SRVS.addAll(res.getServices());
            TIME = res.getTime();
        }else {
            log.warn("receive an expired srv res:{}",res);
        }
    }

    public static String getOneSrv(){
        List<String> servers = new ArrayList<>(SRVS);
        if (servers.isEmpty()){
            throw new RuntimeException("un survive server obtain");
        }
        return servers.get(new Random().nextInt(servers.size()));
    }

}
