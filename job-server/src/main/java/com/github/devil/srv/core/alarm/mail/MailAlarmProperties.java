package com.github.devil.srv.core.alarm.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eric.yao
 * @date 2021/2/26
 **/
@Data
@ConfigurationProperties(prefix = "alarm.email")
public class MailAlarmProperties {

    public final static String MAIL_ENABLE = "alarm.email.enable";

    /**
     * SMTP server host. For instance, `smtp.example.com`.
     */
    private String host;

    /**
     * SMTP server port.
     */
    private Integer port;

    /**
     * Login user of the SMTP server.
     */
    private String username;

    /**
     * Login password of the SMTP server.
     */
    private String password;

    /**
     * Protocol used by the SMTP server.
     */
    private String protocol = "smtp";

    /**
     * receive mail address
     */
    private List<String> sendTo;

    /**
     * 默认编码
     */
    private Charset defaultEncoding = StandardCharsets.UTF_8;

    /**
     * Additional JavaMail Session properties.
     */
    private Map<String, String> properties = new HashMap<>();

}
