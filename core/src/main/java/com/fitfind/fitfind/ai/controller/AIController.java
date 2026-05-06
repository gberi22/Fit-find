package com.fitfind.fitfind.ai.controller;

import com.fitfind.fitfind.ai.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/public/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @GetMapping("/generate")
    public Map<String, String> generate(@RequestParam String message) {
        return Map.of("generation", aiService.chat(message));
    }

    @GetMapping("/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam String message) {
        return aiService.chatStream(message);
    }
}
