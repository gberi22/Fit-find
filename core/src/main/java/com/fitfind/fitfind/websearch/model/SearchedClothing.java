package com.fitfind.fitfind.websearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SearchedClothing(
        @JsonProperty("title") String title,
        @JsonProperty("price") String price,
        @JsonProperty("link") String link,
        @JsonProperty("source") String source,
        @JsonProperty("thumbnail") String picture,
        @JsonProperty("rating") Double rating,
        @JsonProperty("reviews") Long reviews
) {}
