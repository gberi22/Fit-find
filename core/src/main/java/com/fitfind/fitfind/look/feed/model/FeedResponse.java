package com.fitfind.fitfind.look.feed.model;

import com.fitfind.fitfind.look.common.model.LookCardResponse;

import java.util.List;

public record FeedResponse(
        List<LookCardResponse> looks,
        Long totalElements,
        int totalPages
) { }
