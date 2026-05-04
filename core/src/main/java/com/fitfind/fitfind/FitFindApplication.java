package com.fitfind.fitfind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.fitfind")
public class FitFindApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitFindApplication.class, args);
    }

}
