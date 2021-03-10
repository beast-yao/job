package com.github.devil.srv.config;

import com.github.devil.srv.akka.AkkaSrvStart;
import com.github.devil.srv.akka.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author eric.yao
 * @date 2021/2/4
 **/
@Configuration
public class ServerConfig {

    @Bean
    public ServerProperties akServerProperties(){
        return new ServerProperties();
    }


    @Bean
    public AkkaSrvStart akkaSrvStart(ServerProperties akServerProperties){
        return new AkkaSrvStart(akServerProperties);
    }

}
