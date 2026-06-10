package com.fitfind.fitfind.look.common.model.response;

public record LookSummaryResponse(
    Long id,
    String imageUrl,
    boolean published
) { }
