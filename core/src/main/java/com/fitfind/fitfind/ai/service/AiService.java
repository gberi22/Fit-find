package com.fitfind.fitfind.ai.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfind.fitfind.ai.config.AiProperties;
import com.fitfind.fitfind.ai.model.*;
import com.fitfind.fitfind.security.ratelimit.model.RateLimitType;
import com.fitfind.fitfind.security.ratelimit.service.RateLimitService;
import com.fitfind.fitfind.websearch.model.SearchedClothing;
import com.fitfind.fitfind.websearch.service.WebSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.fitfind.fitfind.utils.JsonHelper.parseJsonObject;
import static com.fitfind.fitfind.utils.JsonHelper.textOrNull;
import static com.fitfind.fitfind.utils.PromptHelper.buildSearchQueryPrompt;
import static com.fitfind.fitfind.utils.PromptHelper.pickBestPrompt;


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

    public String chat(String message, String email) {
        rateLimitService.enforceRateLimit(email, RateLimitType.CLIENT_LOGIN);

        ChatCompletionsOptions options = new ChatCompletionsOptions(
                List.of(new ChatMessage(ChatRole.USER).setContent(message))
        ).setModel(aiProperties.getModel());

        ChatCompletions completions = openaiClient.getChatCompletions(aiProperties.getModel(), options);
        return completions.getChoices().getFirst().getMessage().getContent();
    }

    public OutfitSuggestionResponse recommend(OutfitSuggestionRequest prompt) {
        List<Suggestion> suggestions = new ArrayList<>();
        for (ClothingItem category : prompt.clothes()) {
            suggestions.add(recommendForCategory(category, prompt));
        }

        return new OutfitSuggestionResponse(suggestions);
    }

    public Suggestion recommendForCategory(ClothingItem category, OutfitSuggestionRequest prompt) {
        log.info("[recommend] category={} - starting", category);
        Suggestion failedSuggestion = new Suggestion(category, null, null, null, NOT_FOUND_MESSAGE);

        String response;
        try {
            String query = buildSearchQueryPrompt(category, prompt);
            String aiResponse = chat(query, prompt.email());
            response = aiResponse == null ? "" : aiResponse.trim().replaceAll("^[\"']|[\"']$", "");
            log.info("[recommend] category={} - AI built query: '{}'", category, response);
        } catch (RuntimeException e) {
            log.warn("[recommend] category={} - FAIL building query: {}", category, e.getMessage(), e);
            return failedSuggestion;
        }

        if (response.isBlank()) {
            log.warn("[recommend] category={} - FAIL: AI returned empty query", category);
            return failedSuggestion;
        }

        List<SearchedClothing> results;
        try {
            results = webSearchService.searchGoogleShopping(response);
        } catch (RuntimeException e) {
            log.warn("[recommend] category={} - FAIL calling SerpAPI: {}", category, e.getMessage(), e);
            return failedSuggestion;
        }
        log.info("[recommend] category={} - SerpAPI returned {} results", category, results.size());

        if (results.isEmpty()) {
            log.warn("[recommend] category={} - FAIL: SerpAPI returned 0 results for query '{}'", category, response);
            return failedSuggestion;
        }

        List<SearchedClothing> trimmed = results.size() > MAX_RESULTS_FOR_AI
                ? results.subList(0, MAX_RESULTS_FOR_AI)
                : results;

        try {
            Suggestion rec = pickBest(category, prompt, trimmed);
            if (rec.name() == null) {
                log.warn("[recommend] category={} - FAIL: AI selection step produced null name", category);
            } else {
                log.info("[recommend] category={} - SUCCESS: picked '{}'", category, rec.name());
            }
            return rec;
        } catch (RuntimeException e) {
            log.warn("[recommend] category={} - FAIL picking best: {}", category, e.getMessage(), e);
            return failedSuggestion;
        }
    }

    private Suggestion pickBest(ClothingItem category, OutfitSuggestionRequest prompt, List<SearchedClothing> results) {
        Suggestion failedSuggestion = new Suggestion(category, null, null, null, NOT_FOUND_MESSAGE);

        String resultsJson;
        try {
            resultsJson = objectMapper.writeValueAsString(results);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize search results", e);
        }

        String userMessage = pickBestPrompt(resultsJson, category, prompt);

        String response = chat(userMessage, prompt.email());
        log.info("[recommend] category={} - AI selection raw response: {}", category, response);
        JsonNode node = parseJsonObject(response, objectMapper);
        if (node == null) {
            log.warn("[recommend] category={} - FAIL: could not parse AI JSON: {}", category, response);
            return failedSuggestion;
        }

        String name = textOrNull(node.get("name"));
        String link = textOrNull(node.get("link"));
        String picture = textOrNull(node.get("picture"));

        if (name == null || link == null) {
            return failedSuggestion;
        }

        return new Suggestion(category, name, link, picture, null);
    }
}
