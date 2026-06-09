package com.fitfind.fitfind.look.common.model;

import com.fitfind.fitfind.ai.common.model.enums.Gender;
import com.fitfind.fitfind.ai.common.model.enums.Style;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record LookCardResponse(
        Long id,
        String imageUrl,
        List<Style> styles,
        Gender gender,
        BigDecimal budgetMin,
        BigDecimal budgetMax,
        String username,
        LocalDateTime createdAt
) { }
