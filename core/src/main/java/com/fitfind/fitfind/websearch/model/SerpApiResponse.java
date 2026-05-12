package com.fitfind.fitfind.websearch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SerpApiResponse(
        @JsonProperty("shopping_results") List<SearchedClothing> shoppingResults,
        @JsonProperty("error") String error
) { }
