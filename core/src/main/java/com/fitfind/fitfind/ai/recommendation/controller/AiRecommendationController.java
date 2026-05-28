package com.fitfind.fitfind.ai.recommendation.controller;

import com.fitfind.fitfind.ai.model.request.OutfitSuggestionRequest;
import com.fitfind.fitfind.ai.model.response.OutfitSuggestionResponse;
import com.fitfind.fitfind.ai.recommendation.service.AiRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiRecommendationController {

    private final AiRecommendationService aiRecommendationService;

    @PostMapping("/outfit-suggestions")
    public ResponseEntity<OutfitSuggestionResponse> recommend(
        Authentication authentication,
        @RequestBody OutfitSuggestionRequest request
    ) {
        return ResponseEntity.ok(aiRecommendationService.recommend(request, authentication.getName()));
    }
}
