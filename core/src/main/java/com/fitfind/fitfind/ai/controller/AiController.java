package com.fitfind.fitfind.ai.controller;

import com.fitfind.fitfind.ai.model.reqeust.OutfitSuggestionRequest;
import com.fitfind.fitfind.ai.model.response.OutfitSuggestionResponse;
import com.fitfind.fitfind.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/outfit-suggestions")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping
    public OutfitSuggestionResponse recommend(@RequestBody OutfitSuggestionRequest request) {
        return aiService.recommend(request);
    }

}
