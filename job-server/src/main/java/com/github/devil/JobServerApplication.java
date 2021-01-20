package com.github.devil;

import com.github.devil.akka.MainAkServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class JobServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobServerApplication.class,args);

        MainAkServer.start();
    }

}
