package com.fitfind.fitfind.ai.history.controller;

import com.fitfind.fitfind.ai.history.model.response.AiHistoryResponse;
import com.fitfind.fitfind.ai.history.service.AiHistoryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiHistoryController {

    private final AiHistoryService aiHistoryService;

    @GetMapping("/history")
    public ResponseEntity<AiHistoryResponse> history(
        Authentication authentication,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return ResponseEntity.ok(aiHistoryService.list(authentication.getName(), page, size));
    }
}
