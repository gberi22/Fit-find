package com.fitfind.fitfind;

import com.fitfind.fitfind.ai.service.AIService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ConfigurationPropertiesScan("com.fitfind")
public class FitFindApplication {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("ANTHROPIC_API_KEY");
        if (apiKey != null) {
            System.setProperty("ANTHROPIC_API_KEY", apiKey);
        }
        SpringApplication.run(FitFindApplication.class, args);
    }

    @Bean
    public CommandLineRunner testAI(AIService aiService) {
        return args -> {
            String response = aiService.testConnection();
            System.out.println("AI Response: " + response);
        };
    }
}
