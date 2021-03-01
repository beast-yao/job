package com.github.devil.srv.config.alarm;

import com.github.devil.srv.core.alarm.AlarmService;
import com.github.devil.srv.core.alarm.DelegaAlarmService;
import com.github.devil.srv.core.alarm.mail.MailAlarmProperties;
import com.github.devil.srv.core.alarm.mail.MailAlarmServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author eric.yao
 * @date 2021/2/26
 **/
@Slf4j
@Configuration
public class AlarmConfig {

    @Configuration
    @ConditionalOnProperty(value = MailAlarmProperties.MAIL_ENABLE,havingValue = "true")
    public static class MailAram{

        @Bean
        public static MailAlarmProperties mailAlarmProperties(){
            return new MailAlarmProperties();
        }

        @Bean
        public static JavaMailSender javaMailSender(MailAlarmProperties properties){
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(properties.getHost());
            if (properties.getPort() != null) {
                sender.setPort(properties.getPort());
            }
            sender.setUsername(properties.getUsername());
            sender.setPassword(properties.getPassword());
            sender.setProtocol(properties.getProtocol());
            if (properties.getDefaultEncoding() != null) {
                sender.setDefaultEncoding(properties.getDefaultEncoding().name());
            }
            if (!properties.getProperties().isEmpty()) {
                sender.setJavaMailProperties(asProperties(properties.getProperties()));
            }
            return sender;
        }

        static Properties asProperties(Map<String,String> map){
            Properties properties = new Properties();
            properties.putAll(map);
            return properties;
        }


        @Bean
        public static MailAlarmServiceImpl mailAlarmService(JavaMailSender javaMailSender,
                                                     MailAlarmProperties mailAlarmProperties){
            return new MailAlarmServiceImpl(javaMailSender,mailAlarmProperties);
        }
    }


    @Primary
    @Bean(name = {"alarmService","delegaAlarmService"})
    public DelegaAlarmService delegaAlarmService(ObjectProvider<List<AlarmService>> provider){
        List<AlarmService> alarmServices = provider.getIfAvailable(ArrayList::new);
        log.info("current activate alarm [{}]",alarmServices.stream().map(AlarmService::name).collect(Collectors.joining(",")));
        return new DelegaAlarmService(alarmServices);
    }

}
