package com.github.devil.sample;

import com.github.devil.client.spring.annotation.EnableJobClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author eric.yao
 * @date 2021/2/3
 **/
@EnableJobClient
@SpringBootApplication
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class,args);
    }

}
