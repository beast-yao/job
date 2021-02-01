package com.github.devil.srv.akka.ha;

import java.util.Set;

/**
 * @author eric.yao
 * @date 2021/2/1
 **/
public interface ServerChoose {

    /**
     * 选择一个可用服务
     * @param server
     * @return
     */
    public String choose(Set<String> server);

}
