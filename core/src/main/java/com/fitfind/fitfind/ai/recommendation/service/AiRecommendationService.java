package com.fitfind.fitfind.ai.recommendation.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfind.fitfind.ai.common.model.CategorySuggestions;
import com.fitfind.fitfind.ai.common.model.RawImage;
import com.fitfind.fitfind.ai.common.model.Suggestion;
import com.fitfind.fitfind.ai.recommendation.config.AiRecommendationProperties;
import com.fitfind.fitfind.ai.recommendation.config.AiVisionProperties;
import com.fitfind.fitfind.ai.recommendation.exception.CategoryFailedException;
import com.fitfind.fitfind.ai.recommendation.exception.InvalidReferenceImageException;
import com.fitfind.fitfind.ai.history.service.AiHistoryService;
import com.fitfind.fitfind.ai.common.model.enums.ClothingItem;
import com.fitfind.fitfind.ai.common.model.request.OutfitSuggestionRequest;
import com.fitfind.fitfind.ai.common.model.response.OutfitSuggestionResponse;
import com.fitfind.fitfind.security.ratelimit.model.RateLimitType;
import com.fitfind.fitfind.security.ratelimit.service.RateLimitService;
import com.fitfind.fitfind.websearch.model.SearchedClothing;
import com.fitfind.fitfind.websearch.service.WebSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static com.fitfind.fitfind.ai.common.utils.JsonHelper.parseJsonArray;
import static com.fitfind.fitfind.ai.common.utils.JsonHelper.textOrNull;
import static com.fitfind.fitfind.ai.common.utils.PromptHelper.buildSearchQueryPrompt;
import static com.fitfind.fitfind.ai.common.utils.PromptHelper.formatGarmentDescriptions;
import static com.fitfind.fitfind.ai.common.utils.PromptHelper.pickBestPrompt;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiRecommendationService {

    private static final int MAX_RESULTS_FOR_AI = 10;
    private static final int MAX_OPTIONS_PER_CATEGORY = 3;
    private static final String NOT_FOUND_MESSAGE =
        "We couldn't find a matching item for this category. Please try again.";

    private final OpenAIClient openaiClient;
    private final AiRecommendationProperties aiRecommendationProperties;
    private final RateLimitService rateLimitService;
    private final WebSearchService webSearchService;
    private final AiHistoryService aiHistoryService;
    private final StyleAnalysisService styleAnalysisService;
    private final AiVisionProperties aiVisionProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OutfitSuggestionResponse recommend(OutfitSuggestionRequest prompt, String email) {
        rateLimitService.enforceRateLimit(email, RateLimitType.AI_GENERATION);
        OutfitSuggestionRequest effective = withReferenceContext(prompt);
        List<CategorySuggestions> categories = effective.clothes().stream()
            .map(category -> recommendForCategory(category, effective))
            .toList();
        OutfitSuggestionResponse response = new OutfitSuggestionResponse(categories);
        aiHistoryService.record(email, effective, response);
        return response;
    }

    private OutfitSuggestionRequest withReferenceContext(OutfitSuggestionRequest prompt) {
        List<RawImage> images = readImages(prompt.additionalImages());
        if (images.isEmpty()) {
            return prompt;
        }
        List<String> garments = styleAnalysisService.analyze(images, prompt.additionalComments());
        String merged = formatGarmentDescriptions(garments, prompt.additionalComments());
        return new OutfitSuggestionRequest(
            prompt.gender(),
            prompt.size(),
            prompt.clothes(),
            prompt.styles(),
            prompt.minPrice(),
            prompt.maxPrice(),
            merged,
            null
        );
    }

    private List<RawImage> readImages(List<MultipartFile> files) {
        if (files == null) {
            return List.of();
        }
        List<MultipartFile> present = files.stream()
            .filter(file -> file != null && !file.isEmpty())
            .toList();
        if (present.isEmpty()) {
            return List.of();
        }
        int maxImages = aiVisionProperties.getMaxImages();
        if (present.size() > maxImages) {
            throw new InvalidReferenceImageException("Please upload no more than " + maxImages + " images.");
        }
        long maxTotalBytes = aiVisionProperties.getMaxImageSize().toBytes();
        long total = present.stream().mapToLong(MultipartFile::getSize).sum();
        if (total > maxTotalBytes) {
            throw new InvalidReferenceImageException(
                "Uploaded images exceed the " + aiVisionProperties.getMaxImageSize().toMegabytes() + " MB limit."
            );
        }
        return present.stream()
            .map(this::toRawImage)
            .toList();
    }

    private RawImage toRawImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are accepted.");
        }
        try {
            return new RawImage(file.getBytes(), contentType);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read an uploaded image.");
        }
    }

    private CategorySuggestions recommendForCategory(ClothingItem category, OutfitSuggestionRequest prompt) {
        log.info("[recommend] category={} - starting", category);
        try {
            String query = buildQuery(category, prompt);
            List<SearchedClothing> results = searchProducts(category, query);
            List<Suggestion> options = pickBest(category, prompt, results);
            return new CategorySuggestions(category, options, null);
        } catch (CategoryFailedException e) {
            log.warn("[recommend] category={} - FAIL: {}", category, e.getMessage());
            return notFound(category);
        } catch (RuntimeException e) {
            log.warn("[recommend] category={} - FAIL (unexpected): {}", category, e.getMessage(), e);
            return notFound(category);
        }
    }

    private String buildQuery(ClothingItem category, OutfitSuggestionRequest prompt) {
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

    private List<Suggestion> pickBest(
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

        JsonNode array = parseJsonArray(response, objectMapper);
        if (array == null) {
            throw new CategoryFailedException("Could not parse AI JSON array: " + response);
        }

        List<Suggestion> options = StreamSupport.stream(array.spliterator(), false)
            .map(node -> {
                String name = textOrNull(node.get("name"));
                String link = textOrNull(node.get("link"));
                String picture = textOrNull(node.get("picture"));
                if (name == null || link == null) {
                    return null;
                }
                return new Suggestion(category, name, link, picture, null);
            })
            .filter(Objects::nonNull)
            .limit(MAX_OPTIONS_PER_CATEGORY)
            .toList();

        if (options.isEmpty()) {
            throw new CategoryFailedException("AI returned no usable options");
        }

        log.info("[recommend] category={} - SUCCESS: picked {} option(s)", category, options.size());
        return options;
    }

    private CategorySuggestions notFound(ClothingItem category) {
        return new CategorySuggestions(category, List.of(), NOT_FOUND_MESSAGE);
    }

    private String chat(String message) {
        ChatCompletionsOptions options = new ChatCompletionsOptions(
            List.of(new ChatMessage(ChatRole.USER).setContent(message))
        ).setModel(aiRecommendationProperties.getModel());

        ChatCompletions completions = openaiClient.getChatCompletions(aiRecommendationProperties.getModel(), options);
        return completions.getChoices().getFirst().getMessage().getContent();
    }
}
