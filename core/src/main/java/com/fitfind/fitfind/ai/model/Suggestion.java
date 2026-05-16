package com.fitfind.fitfind.ai.model;

public record Suggestion(
        ClothingItem category,
        String name,
        String link,
        String picture,
        String message
) { }
