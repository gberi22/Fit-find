package com.fitfind.fitfind.look.common.model.response;

import java.util.List;

public record LooksPageResponse(
    List<LookSummaryResponse> looks,
    long totalElements,
    int totalPages
) { }
