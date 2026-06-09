package com.fitfind.fitfind.look.common.model.response;

import com.fitfind.fitfind.ai.common.model.enums.Gender;
import com.fitfind.fitfind.ai.common.model.enums.Size;
import com.fitfind.fitfind.ai.common.model.enums.Style;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record LookSummaryResponse(
    Long id,
    Gender gender,
    Size size,
    List<Style> styles,
    BigDecimal budgetMin,
    BigDecimal budgetMax,
    boolean isPublished,
    String imageMimeType,
    LocalDateTime createdAt
) { }
