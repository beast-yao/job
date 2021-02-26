package com.github.devil.srv.core.alarm.mail;

import com.github.devil.srv.core.alarm.AlarmService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author eric.yao
 * @date 2021/2/26
 **/
public class MailAlarmServiceImpl implements AlarmService {

    private JavaMailSender javaMailSender;

    private List<String> sendTo;

    public MailAlarmServiceImpl(JavaMailSender javaMailSender, List<String> sendTo){
        Assert.notNull(sendTo,"please config the receive mail address");
        this.javaMailSender = javaMailSender;
        this.sendTo = sendTo;
    }

    @Override
    public void alarm(String message) {

    }
}
