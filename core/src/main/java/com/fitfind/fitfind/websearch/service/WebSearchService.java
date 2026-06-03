package com.fitfind.fitfind.websearch.service;

import com.fitfind.fitfind.websearch.config.WebSearchProperties;
import com.fitfind.fitfind.websearch.model.SearchedClothing;
import com.fitfind.fitfind.websearch.model.SerpApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSearchService {

    private static final String SEARCH_PATH = "/search.json";
    private static final String GOOGLE_SHOPPING_ENGINE = "google_shopping";
    private static final String PARAM_ENGINE = "engine";
    private static final String PARAM_QUERY = "q";
    private static final String PARAM_API_KEY = "api_key";
    private static final Set<Integer> RETRYABLE_STATUSES = Set.of(429, 500, 502, 503, 504);

    private final WebSearchProperties properties;
    private final RestClient webSearchRestClient;

    public List<SearchedClothing> searchGoogleShopping(String query) {
        int maxAttempts = Math.max(1, properties.getMaxAttempts());
        long backoffMs = properties.getRetryBackoff().toMillis();
        RuntimeException last = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return executeSearch(query);
            } catch (TransientSearchException | ResourceAccessException e) {
                last = e;
                log.warn("[web-search] transient failure (attempt {}/{}): {}", attempt, maxAttempts, e.getMessage());
                if (attempt < maxAttempts) {
                    sleep(backoffMs * attempt);
                }
            }
        }
        throw last;
    }

    private List<SearchedClothing> executeSearch(String query) {
        SerpApiResponse response = webSearchRestClient.get()
                .uri(builder -> builder
                        .path(SEARCH_PATH)
                        .queryParam(PARAM_ENGINE, GOOGLE_SHOPPING_ENGINE)
                        .queryParam(PARAM_QUERY, query)
                        .queryParam(PARAM_API_KEY, properties.getKey())
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    if (RETRYABLE_STATUSES.contains(res.getStatusCode().value())) {
                        throw new TransientSearchException(
                                "SerpAPI transient failure: " + res.getStatusCode());
                    }
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

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while retrying web search", e);
        }
    }

    private static final class TransientSearchException extends RuntimeException {
        private TransientSearchException(String message) {
            super(message);
        }
    }
}
