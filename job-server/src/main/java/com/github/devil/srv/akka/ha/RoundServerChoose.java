package com.github.devil.srv.akka.ha;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author eric.yao
 * @date 2021/2/1
 **/
@Component
public class RoundServerChoose implements ServerChoose {

    private final AtomicInteger round = new AtomicInteger(0);

    @Override
    public String choose(Set<String> server) {
        List<String> servers = new ArrayList<>(server);

        int index = round.getAndIncrement();

        return servers.get(index % servers.size());
    }
}
