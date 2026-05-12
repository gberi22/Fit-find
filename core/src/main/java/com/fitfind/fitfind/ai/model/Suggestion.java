package com.fitfind.fitfind.ai.model;

public record Suggestion(
        ClothingItem category,
        String name,
        String link,
        String picture,
        String message
) {
    public static Suggestion found(ClothingItem category, String name, String link, String picture) {
        return new Suggestion(category, name, link, picture, null);
    }

    public static Suggestion notFound(ClothingItem category, String message) {
        return new Suggestion(category, null, null, null, message);
    }
}
