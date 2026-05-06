package com.fitfind.fitfind.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIService {

    private final AnthropicChatModel chatModel;

    public String testConnection() {
        String response = chatModel.call("Say hello in one sentence.");
        log.info("Test connection response received");
        return response;
    }

    public String chat(String message) {
        return chatModel.call(message);
    }

    public Flux<ChatResponse> chatStream(String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
}
