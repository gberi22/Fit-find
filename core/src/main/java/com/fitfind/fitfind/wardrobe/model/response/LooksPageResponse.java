package com.fitfind.fitfind.wardrobe.model.response;

import java.util.List;

public record LooksPageResponse(
    List<LookSummaryResponse> looks,
    long totalElements,
    int totalPages
) { }
