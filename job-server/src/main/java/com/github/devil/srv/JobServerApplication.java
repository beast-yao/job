package com.github.devil.srv;

import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.banner.JobBanner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class JobServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .banner(new JobBanner())
                .listeners(new ApplicationPidFileWriter())
                .sources(JobServerApplication.class).run(args);

        MainAkServer.start();
    }

}
