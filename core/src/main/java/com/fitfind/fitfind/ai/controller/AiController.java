package com.fitfind.fitfind.ai.controller;

import com.fitfind.fitfind.ai.history.model.response.AiHistoryResponse;
import com.fitfind.fitfind.ai.history.service.AiHistoryService;
import com.fitfind.fitfind.ai.model.reqeust.OutfitSuggestionRequest;
import com.fitfind.fitfind.ai.model.response.OutfitSuggestionResponse;
import com.fitfind.fitfind.ai.service.AiService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final AiHistoryService aiHistoryService;

    @PostMapping("/outfit-suggestions")
    public ResponseEntity<OutfitSuggestionResponse> recommend(
        Authentication authentication,
        @RequestBody OutfitSuggestionRequest request
    ) {
        return ResponseEntity.ok(aiService.recommend(request, authentication.getName()));
    }

    @GetMapping("/history")
    public ResponseEntity<AiHistoryResponse> history(
        Authentication authentication,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return ResponseEntity.ok(aiHistoryService.list(authentication.getName(), page, size));
    }
}
