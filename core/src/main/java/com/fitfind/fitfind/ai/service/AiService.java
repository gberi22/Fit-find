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
import java.util.stream.Collectors;

import static com.fitfind.fitfind.utils.JsonHelper.parseJsonObject;
import static com.fitfind.fitfind.utils.JsonHelper.textOrNull;


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

    private String buildSearchQueryPrompt(ClothingItem category, OutfitSuggestionRequest prompt) {
        String stylesText = prompt.styles().stream()
                .map(Style::name)
                .collect(Collectors.joining(", "));
        if (stylesText.isEmpty()) {
            stylesText = "(no specific style)";
        }

        String userMessage = """
                Build a single Google Shopping search query for the following clothing request.
                Category: %s
                Styles: %s
                Price range: %s - %s
                Additional comments: %s

                Reply with ONLY the query string, no quotes, no extra text, no markdown.
                Keep it concise (under 12 words) and optimized for Google Shopping.
                """.formatted(
                category.name(),
                stylesText,
                prompt.minPrice(),
                prompt.maxPrice(),
                prompt.additionalComments() == null ? "" : prompt.additionalComments()
        );

        String response = chat(userMessage, prompt.email());
        return response == null ? "" : response.trim().replaceAll("^[\"']|[\"']$", "");
    }

    private String pickBestPrompt(String resultsJson, ClothingItem category, OutfitSuggestionRequest prompt) {
        String stylesText = prompt.styles().stream()
                .map(Style::name)
                .collect(Collectors.joining(", "));

        return """
                You are picking the single best clothing item for a user from Google Shopping results.

                User preferences:
                - Category: %s
                - Styles: %s
                - Price range: %s - %s
                - Additional comments: %s

                Search results (JSON):
                %s

                Pick the SINGLE best item that fits the user's preferences and price range.
                Reply with ONLY a valid JSON object, no markdown, no commentary, exactly this shape:
                {"name": "<title>", "link": "<product url>", "picture": "<image url>"}

                Use the values directly from the chosen result item (title -> name, link -> link, picture -> picture).
                If absolutely no item is suitable, reply with: {"name": null, "link": null, "picture": null}
                """.formatted(
                category.name(),
                stylesText,
                prompt.minPrice(),
                prompt.maxPrice(),
                prompt.additionalComments() == null ? "" : prompt.additionalComments(),
                resultsJson
        );
    }

    public OutfitSuggestionResponse recommend(OutfitSuggestionRequest prompt) {
        List<Suggestion> suggestions = new ArrayList<>();
        for (ClothingItem category : prompt.clothes()) {
            suggestions.add(recommendForCategory(category, prompt));
        }

        return new OutfitSuggestionResponse(suggestions);
    }

    private Suggestion recommendForCategory(ClothingItem category, OutfitSuggestionRequest prompt) {
        log.info("[recommend] category={} - starting", category);

        String query;
        try {
            query = buildSearchQueryPrompt(category, prompt);
            log.info("[recommend] category={} - AI built query: '{}'", category, query);
        } catch (RuntimeException e) {
            log.warn("[recommend] category={} - FAIL building query: {}", category, e.getMessage(), e);
            return Suggestion.notFound(category, NOT_FOUND_MESSAGE);
        }

        if (query.isBlank()) {
            log.warn("[recommend] category={} - FAIL: AI returned empty query", category);
            return Suggestion.notFound(category, NOT_FOUND_MESSAGE);
        }

        List<SearchedClothing> results;
        try {
            results = webSearchService.searchGoogleShopping(query);
        } catch (RuntimeException e) {
            log.warn("[recommend] category={} - FAIL calling SerpAPI: {}", category, e.getMessage(), e);
            return Suggestion.notFound(category, NOT_FOUND_MESSAGE);
        }
        log.info("[recommend] category={} - SerpAPI returned {} results", category, results.size());

        if (results.isEmpty()) {
            log.warn("[recommend] category={} - FAIL: SerpAPI returned 0 results for query '{}'", category, query);
            return Suggestion.notFound(category, NOT_FOUND_MESSAGE);
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
            return Suggestion.notFound(category, NOT_FOUND_MESSAGE);
        }
    }

    private Suggestion pickBest(ClothingItem category, OutfitSuggestionRequest prompt, List<SearchedClothing> results) {
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
            return Suggestion.notFound(category, NOT_FOUND_MESSAGE);
        }

        String name = textOrNull(node.get("name"));
        String link = textOrNull(node.get("link"));
        String picture = textOrNull(node.get("picture"));

        if (name == null || link == null) {
            return Suggestion.notFound(category, NOT_FOUND_MESSAGE);
        }
        return Suggestion.found(category, name, link, picture);
    }
}