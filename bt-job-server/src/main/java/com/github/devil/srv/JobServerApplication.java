package com.github.devil.srv;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@EnableScheduling
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class JobServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(JobServerApplication.class)
                .run(args);
    }

}
