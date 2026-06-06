package com.fitfind.fitfind.feed.model;

import java.time.LocalDateTime;
import java.util.List;

public record GeneralFeedResponse(
        List<LookCardResponse> looks,
        Long totalElements,
        int totalPages
) { }
