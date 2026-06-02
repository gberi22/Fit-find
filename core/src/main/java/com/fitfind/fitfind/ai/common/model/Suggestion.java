package com.fitfind.fitfind.ai.common.model;

import com.fitfind.fitfind.ai.common.model.enums.ClothingItem;

public record Suggestion(
    ClothingItem category,
    String name,
    String link,
    String picture,
    String message
) { }
