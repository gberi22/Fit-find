package com.fitfind.fitfind.ai.history.model.response;

import com.fitfind.fitfind.ai.model.request.OutfitSuggestionRequest;
import com.fitfind.fitfind.ai.model.response.OutfitSuggestionResponse;

import java.time.LocalDateTime;
import java.util.List;

public record AiHistoryResponse(
    List<HistoryItem> history,
    Long totalElements,
    int totalPages
) {
    public record HistoryItem(
        Long id,
        OutfitSuggestionRequest request,
        OutfitSuggestionResponse response,
        LocalDateTime createdAt
    ) { }
}
