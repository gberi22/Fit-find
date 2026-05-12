package com.fitfind.fitfind.websearch.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fitfind.fitfind.websearch.config.WebSearchConfig;
import com.fitfind.fitfind.websearch.model.SearchedClothing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSearchService {

    private final WebSearchConfig config;
    private final RestClient webSearchRestClient;

    public List<SearchedClothing> searchGoogleShopping(String query) {
        SerpApiResponse response = webSearchRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("serpapi.com")
                        .path("/search.json")
                        .queryParam("engine", "google_shopping")
                        .queryParam("q", query)
                        .queryParam("gl", config.getCountry())
                        .queryParam("hl", config.getLanguage())
                        .queryParam("api_key", config.getKey())
                        .build())
                .retrieve()
                .body(SerpApiResponse.class);

        return response != null && response.searchedClothingItems() != null
                ? response.searchedClothingItems()
                : List.of();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SerpApiResponse(
            @JsonProperty("shopping_results") List<SearchedClothing> searchedClothingItems
    ) {}
}
