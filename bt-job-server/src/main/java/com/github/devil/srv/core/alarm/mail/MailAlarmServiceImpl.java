package com.github.devil.srv.core.alarm.mail;

import com.github.devil.srv.core.alarm.Message;
import com.github.devil.srv.core.alarm.AlarmService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Date;

/**
 * @author eric.yao
 * @date 2021/2/26
 **/
public class MailAlarmServiceImpl implements AlarmService {

    private JavaMailSender javaMailSender;

    private MailAlarmProperties mailAlarmProperties;

    public MailAlarmServiceImpl(JavaMailSender javaMailSender, MailAlarmProperties mailAlarmProperties){
        this.javaMailSender = javaMailSender;
        this.mailAlarmProperties = mailAlarmProperties;
    }

    @Override
    public void alarm(Message message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailAlarmProperties.getSendTo().toArray(new String[0]));
        mailMessage.setSubject(message.getTitle());
        mailMessage.setSubject(message.getContent());
        mailMessage.setSentDate(new Date());
        mailMessage.setFrom(mailAlarmProperties.getUsername());
        javaMailSender.send(mailMessage);
    }

    @Override
    public String name() {
        return "MAIL";
    }
}
