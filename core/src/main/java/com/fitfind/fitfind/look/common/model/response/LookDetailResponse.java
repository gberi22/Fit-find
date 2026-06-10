package com.fitfind.fitfind.look.common.model.response;

import com.fitfind.fitfind.ai.common.model.enums.Gender;
import com.fitfind.fitfind.ai.common.model.enums.Size;
import com.fitfind.fitfind.ai.common.model.enums.Style;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record LookDetailResponse(
    Long id,
    String imageUrl,
    List<Style> styles,
    Gender gender,
    Size size,
    BigDecimal budgetMin,
    BigDecimal budgetMax,
    boolean published,
    LocalDateTime createdAt,
    List<ProductResponse> products
) { }
