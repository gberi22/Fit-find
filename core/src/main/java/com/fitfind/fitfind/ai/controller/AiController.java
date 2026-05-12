package com.fitfind.fitfind.ai.controller;

import com.fitfind.fitfind.ai.model.OutfitSuggestionRequest;
import com.fitfind.fitfind.ai.model.OutfitSuggestionResponse;
import com.fitfind.fitfind.ai.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai/outfit-suggestions")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping
    public OutfitSuggestionResponse createOutfitSuggestion(
        Authentication authentication,
        @Valid @RequestBody OutfitSuggestionRequest request
    ) {
        return aiService.chat(request, authentication.getName());
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamOutfitSuggestion(
        Authentication authentication,
        @Valid @RequestBody OutfitSuggestionRequest request
    ) {
        return aiService.chatStream(request, authentication.getName());
    }
}
