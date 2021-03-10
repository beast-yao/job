package com.github.devil.srv.config;

import com.github.devil.common.CommonConstants;
import com.google.common.base.Predicates;
import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * @author eric.yao
 * @date 2021/2/18
 **/
@Profile({"mysql","embedded"})
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SPRING_WEB)
                .apiInfo(apiInfo())
                .select()
                .apis(Predicates.or(RequestHandlerSelectors.withClassAnnotation(RestController.class), RequestHandlerSelectors.withClassAnnotation(Api.class)))
                .paths(PathSelectors.ant(CommonConstants.BASE_CONTROLLER_PATH))
                .build()
                .useDefaultResponseMessages(false)
                .enableUrlTemplating(true)
                ;

    }

    private ApiInfo apiInfo(){
        return new ApiInfo("Job Task Center", "Task Center", "V1.0", null, contact(), null, null, Collections.emptyList());
    }

    private Contact contact(){
        return new Contact("Devil", "", "");
    }
}
