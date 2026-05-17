package com.fitfind.fitfind.ai.model;

import com.fitfind.fitfind.ai.model.enums.ClothingItem;

public record Suggestion(
        ClothingItem category,
        String name,
        String link,
        String picture,
        String message
) { }
