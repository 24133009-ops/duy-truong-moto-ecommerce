package com.duytruongmoto.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DuyTruongMotoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DuyTruongMotoApplication.class, args);
    }
}
