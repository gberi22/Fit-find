package com.fitfind.fitfind.ai.recommendation.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiVisionConfig {

    @Bean
    public ChatClient visionChatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
