package com.fitfind.fitfind.ai.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.fitfind.fitfind.ai.config.AIConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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

    public String chat(String message) {
        ChatCompletionsOptions options = new ChatCompletionsOptions(
                List.of(new ChatMessage(ChatRole.USER).setContent(message))
        );

        ChatCompletions completions = openaiClient.getChatCompletions(githubModel, options);
        return completions.getChoices().getFirst().getMessage().getContent();
    }

    public Flux<String> chatStream(String message) {
        ChatCompletionsOptions options = new ChatCompletionsOptions(
                List.of(new ChatMessage(ChatRole.USER).setContent(message))
        );

        return Flux.fromStream(openaiClient.getChatCompletionsStream(githubModel, options).stream())
                .flatMap(chunk -> {
                    List<ChatChoice> choices = chunk.getChoices();
                    if (choices == null || choices.isEmpty()) {
                        return Flux.empty();
                    }
                    ChatMessageDelta delta = choices.getFirst().getDelta();
                    String content = delta != null ? delta.getContent() : null;
                    return content != null ? Flux.just(content) : Flux.empty();
                });
    }
}
