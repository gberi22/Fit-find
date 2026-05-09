package com.fitfind.fitfind;

import com.fitfind.fitfind.ai.service.AIService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ConfigurationPropertiesScan("com.fitfind")
public class FitFindApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitFindApplication.class, args);
    }

    @Bean
    public CommandLineRunner testAI(AIService aiService) {
        return args -> {
            aiService.testConnection();
        };
    }
}
