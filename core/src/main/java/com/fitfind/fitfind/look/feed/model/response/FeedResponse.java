package com.fitfind.fitfind.look.feed.model.response;

import com.fitfind.fitfind.look.common.model.response.LookSummaryResponse;

import java.util.List;

public record FeedResponse(
        List<LookSummaryResponse> looks,
        Long totalElements,
        int totalPages
) { }
