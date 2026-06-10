package com.fitfind.fitfind.look.feed.model.request;

import com.fitfind.fitfind.ai.common.model.enums.Gender;
import com.fitfind.fitfind.ai.common.model.enums.Style;

import java.math.BigDecimal;
import java.util.List;

public record FeedFiltersRequest(
       Gender gender,
       List<Style> style,
       BigDecimal minBudget,
       BigDecimal maxBudget
) { }
