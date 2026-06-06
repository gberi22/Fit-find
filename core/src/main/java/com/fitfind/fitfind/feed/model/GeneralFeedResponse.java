package com.fitfind.fitfind.feed.model;

import java.time.LocalDateTime;
import java.util.List;

public record GeneralFeedResponse(
        List<LookItem> looks,
        Long totalElements,
        int totalPages
) {
    public record LookItem(
            Long id,
            LocalDateTime createdAt
    ) { }
}
