package com.fitfind.fitfind.feed.model;

import java.util.List;

public record FeedResponse(
        List<LookCardResponse> looks,
        Long totalElements,
        int totalPages
) { }
