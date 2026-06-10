package com.fitfind.fitfind.look.profile.model.request;

import com.fitfind.fitfind.ai.common.model.Suggestion;
import com.fitfind.fitfind.ai.common.model.enums.Gender;
import com.fitfind.fitfind.ai.common.model.enums.Size;
import com.fitfind.fitfind.ai.common.model.enums.Style;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record SaveLookRequest(
    @NotNull Gender gender,
    Size size,
    @NotEmpty List<Style> styles,
    BigDecimal budgetMin,
    BigDecimal budgetMax,
    @NotEmpty List<Suggestion> suggestions,
    @NotBlank String imageBase64,
    @NotBlank String imageMimeType
) { }
