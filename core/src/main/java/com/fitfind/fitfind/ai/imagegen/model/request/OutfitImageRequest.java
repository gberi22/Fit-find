package com.fitfind.fitfind.ai.imagegen.model.request;

import com.fitfind.fitfind.ai.common.model.Suggestion;
import com.fitfind.fitfind.ai.common.model.enums.Gender;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OutfitImageRequest(
        @NotNull Gender gender,
        @NotEmpty List<Suggestion> suggestions
) { }
