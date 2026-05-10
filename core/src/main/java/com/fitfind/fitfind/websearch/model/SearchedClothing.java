package com.fitfind.fitfind.websearch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SearchedClothing(
        String title,
        String price,
        String link,
        String source,
        String picture,
        Double rating,
        @JsonProperty("reviews") Integer reviews
) {}
