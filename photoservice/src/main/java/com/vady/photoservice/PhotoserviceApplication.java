package com.vady.photoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PhotoserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoserviceApplication.class, args);
    }

}
