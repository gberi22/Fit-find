package com.fitfind.fitfind.look.common.model.response;

import java.util.List;

public record LooksResponse(
    List<LookSummaryResponse> looks
) { }
