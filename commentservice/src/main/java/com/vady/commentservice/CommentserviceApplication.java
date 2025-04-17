package com.vady.commentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(AppInfo.class)
public class CommentserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommentserviceApplication.class, args);
    }

}
