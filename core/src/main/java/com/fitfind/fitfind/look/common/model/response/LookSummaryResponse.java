package com.fitfind.fitfind.look.common.model.response;

import java.time.LocalDateTime;

public record LookSummaryResponse(
    Long id,
    String imageUrl,
    boolean published,
    LocalDateTime createdAt
) { }
