package com.fitfind.fitfind.ai.common.model;

import com.fitfind.fitfind.ai.common.model.enums.ClothingItem;

import java.util.List;

public record CategorySuggestions(
        ClothingItem category,
        List<Suggestion> options,
        String message
) { }
