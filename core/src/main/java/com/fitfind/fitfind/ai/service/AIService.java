package com.fitfind.fitfind.ai.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.azure.core.credential.AzureKeyCredential;
import com.fitfind.fitfind.ai.config.AIConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
public class AIService {

    private final OpenAIClient openaiClient;
    private final String githubModel;

    public AIService(AIConfig aiConfig) {
        this.githubModel = aiConfig.getModel();

        this.openaiClient = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(aiConfig.getKey()))
                .endpoint(aiConfig.getEndpoint())
                .buildClient();
    }

//    public void testConnection() {
//        ChatCompletionsOptions chat = new ChatCompletionsOptions(
//                Arrays.asList(
//                        new ChatMessage(ChatRole.SYSTEM).setContent("You are a helpful assistant."),
//                        new ChatMessage(ChatRole.USER).setContent("Tell me 3 jokes about trains")
//                )
//
//        );
//
//        chat.setModel(githubModel);
//
//        ChatCompletions completions = openaiClient.getChatCompletions(githubModel, chat);
//        String reply = completions.getChoices().getFirst().getMessage().getContent();
//        System.out.println("AI Response: " + reply);
//    }



}
