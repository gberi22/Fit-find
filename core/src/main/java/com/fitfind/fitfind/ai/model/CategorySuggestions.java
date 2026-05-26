package com.fitfind.fitfind.ai.model;

import com.fitfind.fitfind.ai.model.enums.ClothingItem;

import java.util.List;

public record CategorySuggestions(
        ClothingItem category,
        List<Suggestion> options,
        String message
) { }
