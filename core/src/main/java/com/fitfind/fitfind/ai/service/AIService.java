package com.fitfind.fitfind.ai.service;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestUserMessage;
import com.fitfind.fitfind.ai.config.AIConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.azure.core.credential.AzureKeyCredential;

import java.util.*;


@Service
@Slf4j
public class AIService {

    private final ChatCompletionsClient openaiClient;
    private final String githubModel;

    public AIService(AIConfig config) {
        this.openaiClient = new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(config.getKey()))
                .endpoint(config.getEndpoint())
                .buildClient();
        this.githubModel = config.getModel();
        log.info("AI Service initialized with endpoint: {}", config.getEndpoint());
    }

    public String testConnection() {
        List<ChatRequestMessage> messages = List.of(
                new ChatRequestUserMessage("Say hello in one sentence.")
        );

        ChatCompletionsOptions options = new ChatCompletionsOptions(messages);

        var response = openaiClient.complete(options);

        return response.getChoices().getFirst().getMessage().getContent();
    }

}
