package com.fitfind.fitfind.look.feed.model.response;

import com.fitfind.fitfind.look.common.model.response.LookCardResponse;

import java.util.List;

public record FeedResponse(
        List<LookCardResponse> looks,
        Long totalElements,
        int totalPages
) { }
