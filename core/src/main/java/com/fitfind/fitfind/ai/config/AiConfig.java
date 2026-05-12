package com.fitfind.fitfind.ai.config;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public OpenAIClient openAIClient(AiProperties properties) {
        return new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(properties.getKey()))
                .endpoint(properties.getEndpoint())
                .buildClient();
    }
}
