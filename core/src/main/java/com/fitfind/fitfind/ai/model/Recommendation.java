package com.fitfind.fitfind.ai.model;

public record Recommendation(
        Clothes category,
        String name,
        String link,
        String picture,
        String message
) {
    public static Recommendation found(Clothes category, String name, String link, String picture) {
        return new Recommendation(category, name, link, picture, null);
    }

    public static Recommendation notFound(Clothes category, String message) {
        return new Recommendation(category, null, null, null, message);
    }
}
