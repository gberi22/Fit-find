package com.fitfind.fitfind.websearch.service;

import com.fitfind.fitfind.websearch.config.WebSearchProperties;
import com.fitfind.fitfind.websearch.model.SearchedClothing;
import com.fitfind.fitfind.websearch.model.SerpApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSearchService {

    private static final String SEARCH_PATH = "/search.json";
    private static final String GOOGLE_SHOPPING_ENGINE = "google_shopping";
    private static final String PARAM_ENGINE = "engine";
    private static final String PARAM_QUERY = "q";
    private static final String PARAM_API_KEY = "api_key";

    private final WebSearchProperties properties;
    private final RestClient webSearchRestClient;

    public List<SearchedClothing> searchGoogleShopping(String query) {
        SerpApiResponse response = webSearchRestClient.get()
                .uri(builder -> builder
                        .path(SEARCH_PATH)
                        .queryParam(PARAM_ENGINE, GOOGLE_SHOPPING_ENGINE)
                        .queryParam(PARAM_QUERY, query)
                        .queryParam(PARAM_API_KEY, properties.getKey())
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new IllegalStateException(
                            "SerpAPI request failed: " + res.getStatusCode());
                })
                .body(SerpApiResponse.class);

        if (response == null) {
            return List.of();
        }
        if (response.error() != null) {
            throw new IllegalStateException("SerpAPI error: " + response.error());
        }
        return response.shoppingResults() != null ? response.shoppingResults() : List.of();
    }
}
