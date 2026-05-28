package com.fitfind.fitfind.ai.model.request;

import com.fitfind.fitfind.ai.model.enums.ClothingItem;
import com.fitfind.fitfind.ai.model.enums.Gender;
import com.fitfind.fitfind.ai.model.enums.Size;
import com.fitfind.fitfind.ai.model.enums.Style;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record OutfitSuggestionRequest(
        @NotNull Gender gender,
        @NotNull Size size,
        @NotNull List<ClothingItem> clothes,
        @NotNull List<Style> styles,
        @NotNull @DecimalMin("0.00") BigDecimal minPrice,
        @NotNull @DecimalMin("0.00") BigDecimal maxPrice,
        String additionalComments
) { }
