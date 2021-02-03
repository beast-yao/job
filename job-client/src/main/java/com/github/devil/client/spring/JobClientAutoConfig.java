package com.github.devil.client.spring;

import com.github.devil.client.akka.AkkaProperties;
import com.github.devil.client.akka.ClientAkkaServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
public class JobClientAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = AkkaProperties.PREFIX)
    public AkkaProperties akkaProperties(){
        return new AkkaProperties();
    }


    @Bean
    @ConditionalOnMissingBean
    public ClientAkkaServer clientAkkaServer(AkkaProperties akkaProperties){
        ClientAkkaServer clientAkkaServer = new ClientAkkaServer();
        clientAkkaServer.init(akkaProperties);
        return clientAkkaServer;
    }


    @Bean
    public static ScheduleBeanPostProcess scheduleBeanPostProcess(){
        return new ScheduleBeanPostProcess();
    }
}
