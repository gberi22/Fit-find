package com.fitfind.fitfind.utils;

import com.fitfind.fitfind.ai.model.ClothingItem;
import com.fitfind.fitfind.ai.model.OutfitSuggestionRequest;
import com.fitfind.fitfind.ai.model.Style;

import java.util.stream.Collectors;

public class PromptHelper {
    public static String buildSearchQueryPrompt(ClothingItem category, OutfitSuggestionRequest prompt) {
        String stylesText = prompt.styles().stream()
                .map(Style::name)
                .collect(Collectors.joining(", "));
        if (stylesText.isEmpty()) {
            stylesText = "(no specific style)";
        }

        return """
                Build a single Google Shopping search query for the following clothing request.
                Gender: %s
                Size: %s
                Category: %s
                Styles: %s
                Price range: %s - %s
                Additional comments: %s

                Reply with ONLY the query string, no quotes, no extra text, no markdown.
                Keep it concise (under 12 words) and optimized for Google Shopping.
                """.formatted(
                prompt.gender(),
                prompt.size(),
                category.name(),
                stylesText,
                prompt.minPrice(),
                prompt.maxPrice(),
                prompt.additionalComments() == null ? "" : prompt.additionalComments()
        );
    }

    public static String pickBestPrompt(String resultsJson, ClothingItem category, OutfitSuggestionRequest prompt) {
        String stylesText = prompt.styles().stream()
                .map(Style::name)
                .collect(Collectors.joining(", "));

        return """
                You are picking the single best clothing item for a user from Google Shopping results.

                User preferences:
                - Gender: %s
                - Size: %s
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
                prompt.gender(),
                prompt.size(),
                category.name(),
                stylesText,
                prompt.minPrice(),
                prompt.maxPrice(),
                prompt.additionalComments() == null ? "" : prompt.additionalComments(),
                resultsJson
        );
    }
}
