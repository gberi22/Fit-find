package com.fitfind.fitfind.look.common.model.response;

// todo: only look id and image are needed for summary look on profile page
public record LookSummaryResponse(
    Long id,
    String imageMimeType
) { }
