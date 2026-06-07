package com.fitfind.fitfind.feed.model;

import com.fitfind.fitfind.ai.common.model.enums.Gender;
import com.fitfind.fitfind.ai.common.model.enums.Style;

import java.math.BigDecimal;
import java.util.List;

public record FeedRequestFilters(
       Gender gender,
       List<Style> style,
       BigDecimal minBudget,
       BigDecimal maxBudget
) { }
