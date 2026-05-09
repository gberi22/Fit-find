package com.fitfind.fitfind.ai.service;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.*;
import com.azure.core.util.Configuration;
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

    public AIService() {
        String key = Configuration.getGlobalConfiguration().get("GITHUB_MODELS_TOKEN");
        String endpoint = "https://models.github.ai/inference";
        this.githubModel = "openai/gpt-5-mini";

        this.openaiClient = new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(key))
                .endpoint(endpoint)
                .buildClient();
        log.info("AI Service initialized with endpoint: {}", endpoint);
    }

    public void testConnection() {
//        List<ChatRequestMessage> chatMessages = Arrays.asList(
//                new ChatRequestSystemMessage("You are a helpful assistant."),
//                new ChatRequestUserMessage("Tell me 3 jokes about trains")
//        );
//
//        ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages);
//        chatCompletionsOptions.setModel(this.githubModel);
//
//        ChatCompletions completions = this.openaiClient.complete(chatCompletionsOptions);
//
//        System.out.printf("%s.%n", completions.getChoices().getFirst().getMessage().getContent());
        ChatCompletionsOptions options = new ChatCompletionsOptions(
                List.of(new ChatRequestUserMessage("Say hello!"))
        ).setModel(this.githubModel);

        ChatCompletions completions = this.openaiClient.complete(options);

        String reply = completions.getChoices().getFirst().getMessage().getContent();
        System.out.println("AI Response: " + reply);
    }



}
