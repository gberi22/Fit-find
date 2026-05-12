package com.fitfind.fitfind.ai.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatMessageDelta;
import com.azure.ai.openai.models.ChatRole;
import com.fitfind.fitfind.ai.config.AiProperties;
import com.fitfind.fitfind.ai.model.OutfitSuggestionRequest;
import com.fitfind.fitfind.ai.model.OutfitSuggestionResponse;
import com.fitfind.fitfind.security.ratelimit.model.RateLimitType;
import com.fitfind.fitfind.security.ratelimit.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class AiService {

    private final OpenAIClient openaiClient;
    private final AiProperties aiProperties;
    private final RateLimitService rateLimitService;

    public OutfitSuggestionResponse chat(OutfitSuggestionRequest prompt, String email) {

        rateLimitService.enforceRateLimit(email, RateLimitType.CLIENT_LOGIN);
        ChatCompletionsOptions options = new ChatCompletionsOptions(
                List.of(new ChatMessage(ChatRole.USER).setContent(buildMessage(prompt)))
        );

        ChatCompletions completions = openaiClient.getChatCompletions(aiProperties.getModel(), options);
        List<ChatChoice> choices = completions.getChoices();
        String content = "";
        if (choices != null && !choices.isEmpty()) {
            content = choices.getFirst().getMessage().getContent();
        }
        return new OutfitSuggestionResponse(content);
    }

    public Flux<String> chatStream(OutfitSuggestionRequest prompt, String email) {
        rateLimitService.enforceRateLimit(email, RateLimitType.AI_GENERATION);
        ChatCompletionsOptions options = new ChatCompletionsOptions(
                List.of(new ChatMessage(ChatRole.USER).setContent(buildMessage(prompt)))
        );

        return Flux.fromStream(openaiClient.getChatCompletionsStream(aiProperties.getModel(), options).stream())
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

    private String buildMessage(OutfitSuggestionRequest prompt) {
        return "Clothes: " + prompt.clothes()
                + ", Styles: " + prompt.styles()
                + ", Budget: " + prompt.minPrice() + "-" + prompt.maxPrice()
                + ", Notes: " + prompt.additionalComments();
    }
}
