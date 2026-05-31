package com.fitfind.fitfind.ai.recommendation.controller;

import com.fitfind.fitfind.ai.common.model.request.OutfitSuggestionRequest;
import com.fitfind.fitfind.ai.common.model.response.OutfitSuggestionResponse;
import com.fitfind.fitfind.ai.recommendation.service.AiRecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiRecommendationController {

    private final AiRecommendationService aiRecommendationService;

    @PostMapping(value = "/outfit-suggestions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OutfitSuggestionResponse> recommend(
        Authentication authentication,
        @Valid @ModelAttribute OutfitSuggestionRequest request
    ) {
        return ResponseEntity.ok(aiRecommendationService.recommend(request, authentication.getName()));
    }
}
