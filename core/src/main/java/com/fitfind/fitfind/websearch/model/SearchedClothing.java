package com.fitfind.fitfind.websearch.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SearchedClothing(
        String title,
        String price,
        String link,
        String source,
        String picture,
        Double rating,
        Long reviews
) {

    @JsonCreator
    public static SearchedClothing fromSerpApi(
            @JsonProperty("title") String title,
            @JsonProperty("price") String price,
            @JsonProperty("link") String link,
            @JsonProperty("product_link") String productLink,
            @JsonProperty("source") String source,
            @JsonProperty("thumbnail") String picture,
            @JsonProperty("rating") Double rating,
            @JsonProperty("reviews") Long reviews
    ) {
        return new SearchedClothing(
                title,
                price,
                link != null ? link : productLink,
                source,
                picture,
                rating,
                reviews
        );
    }
}
