package com.fitfind.fitfind.ai.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record OutfitSuggestionRequest(
        @NotNull List<ClothingItem> clothes,
        @NotNull List<Style> styles,
        @NotNull @DecimalMin("0.00") BigDecimal minPrice,
        @NotNull @DecimalMin("0.00") BigDecimal maxPrice,
        String additionalComments
) { }
