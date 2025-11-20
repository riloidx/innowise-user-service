package com.innowise.userservice;

import com.innowise.userservice.config.CardProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CardProperties.class)
public class InnowiseUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InnowiseUserServiceApplication.class, args);
    }

}
