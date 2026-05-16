package com.fitfind.fitfind.ai.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfind.fitfind.ai.config.AiProperties;
import com.fitfind.fitfind.ai.exception.CategoryFailedException;
import com.fitfind.fitfind.ai.model.*;
import com.fitfind.fitfind.ai.model.enums.ClothingItem;
import com.fitfind.fitfind.ai.model.reqeust.OutfitSuggestionRequest;
import com.fitfind.fitfind.ai.model.response.OutfitSuggestionResponse;
import com.fitfind.fitfind.security.ratelimit.model.RateLimitType;
import com.fitfind.fitfind.security.ratelimit.service.RateLimitService;
import com.fitfind.fitfind.websearch.model.SearchedClothing;
import com.fitfind.fitfind.websearch.service.WebSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.fitfind.fitfind.ai.utils.JsonHelper.parseJsonObject;
import static com.fitfind.fitfind.ai.utils.JsonHelper.textOrNull;
import static com.fitfind.fitfind.ai.utils.PromptHelper.buildSearchQueryPrompt;
import static com.fitfind.fitfind.ai.utils.PromptHelper.pickBestPrompt;


@Service
@Slf4j
@RequiredArgsConstructor
public class AiService {

    private static final int MAX_RESULTS_FOR_AI = 10;
    private static final String NOT_FOUND_MESSAGE =
            "We couldn't find a matching item for this category. Please try again.";

    private final OpenAIClient openaiClient;
    private final AiProperties aiProperties;
    private final RateLimitService rateLimitService;
    private final WebSearchService webSearchService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OutfitSuggestionResponse recommend(OutfitSuggestionRequest prompt, String email) {
        rateLimitService.enforceRateLimit(email, RateLimitType.AI_GENERATION);
        List<Suggestion> suggestions = prompt.clothes().stream()
                .map(category -> recommendForCategory(category, prompt, email))
                .toList();
        return new OutfitSuggestionResponse(suggestions);
    }

    private Suggestion recommendForCategory(ClothingItem category, OutfitSuggestionRequest prompt, String email) {
        log.info("[recommend] category={} - starting", category);
        try {
            String query = buildQuery(category, prompt, email);
            List<SearchedClothing> results = searchProducts(category, query);
            return pickBest(category, prompt, results);
        } catch (CategoryFailedException e) {
            log.warn("[recommend] category={} - FAIL: {}", category, e.getMessage());
            return notFound(category);
        } catch (RuntimeException e) {
            log.warn("[recommend] category={} - FAIL (unexpected): {}", category, e.getMessage(), e);
            return notFound(category);
        }
    }

    private String buildQuery(ClothingItem category, OutfitSuggestionRequest prompt, String email) {
        String searchQuery = buildSearchQueryPrompt(category, prompt);
        String raw = chat(searchQuery);
        String query = raw == null ? "" : raw.trim().replaceAll("^[\"']|[\"']$", "");
        if (query.isBlank()) {
            throw new CategoryFailedException("AI returned empty query");
        }
        log.info("[recommend] category={} - query: '{}'", category, query);
        return query;
    }

    private List<SearchedClothing> searchProducts(ClothingItem category, String query) {
        List<SearchedClothing> results = webSearchService.searchGoogleShopping(query);
        log.info("[recommend] category={} - SerpAPI returned {} results", category, results.size());
        if (results.isEmpty()) {
            throw new CategoryFailedException("SerpAPI returned 0 results for '" + query + "'");
        }
        return results.size() > MAX_RESULTS_FOR_AI ? results.subList(0, MAX_RESULTS_FOR_AI) : results;
    }

    private Suggestion pickBest(
        ClothingItem category,
        OutfitSuggestionRequest prompt,
        List<SearchedClothing> results
    ) {
        String resultsJson;
        try {
            resultsJson = objectMapper.writeValueAsString(results);
        } catch (Exception e) {
            throw new CategoryFailedException("Failed to serialize search results", e);
        }

        String response = chat(pickBestPrompt(resultsJson, category, prompt));
        log.info("[recommend] category={} - AI selection: {}", category, response);

        JsonNode node = parseJsonObject(response, objectMapper);
        if (node == null) {
            throw new CategoryFailedException("Could not parse AI JSON: " + response);
        }

        String name = textOrNull(node.get("name"));
        String link = textOrNull(node.get("link"));
        String picture = textOrNull(node.get("picture"));
        if (name == null || link == null) {
            throw new CategoryFailedException("AI selection missing name or link");
        }

        log.info("[recommend] category={} - SUCCESS: picked '{}'", category, name);
        return new Suggestion(category, name, link, picture, null);
    }

    private Suggestion notFound(ClothingItem category) {
        return new Suggestion(category, null, null, null, NOT_FOUND_MESSAGE);
    }

    private String chat(String message) {
        ChatCompletionsOptions options = new ChatCompletionsOptions(
                List.of(new ChatMessage(ChatRole.USER).setContent(message))
        ).setModel(aiProperties.getModel());

        ChatCompletions completions = openaiClient.getChatCompletions(aiProperties.getModel(), options);
        return completions.getChoices().getFirst().getMessage().getContent();
    }
}
