package com.github.devil.work;

import com.github.devil.client.spring.annotation.EnableJobClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * create by Yao 2021/10/21
 **/
@EnableJobClient
@SpringBootApplication
public class WorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class,args);
    }

}
