package com.fitfind.fitfind.ai.utils;

import com.fitfind.fitfind.ai.model.Suggestion;
import com.fitfind.fitfind.ai.model.enums.ClothingItem;
import com.fitfind.fitfind.ai.model.enums.Gender;
import com.fitfind.fitfind.ai.model.reqeust.OutfitSuggestionRequest;
import com.fitfind.fitfind.ai.model.enums.Style;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                You are picking the top 3 best clothing items for a user from Google Shopping results.

                User preferences:
                - Gender: %s
                - Size: %s
                - Category: %s
                - Styles: %s
                - Price range: %s - %s
                - Additional comments: %s

                Search results (JSON):
                %s

                Pick the THREE best items that fit the user's preferences and price range,
                ordered from best to worst. The three items must be distinct.
                Reply with ONLY a valid JSON array, no markdown, no commentary, exactly this shape:
                [
                  {"name": "<title>", "link": "<product url>", "picture": "<image url>"},
                  {"name": "<title>", "link": "<product url>", "picture": "<image url>"},
                  {"name": "<title>", "link": "<product url>", "picture": "<image url>"}
                ]

                Use the values directly from each chosen result item (title -> name, link -> link, picture -> picture).
                If fewer than 3 suitable items exist, return as many as are suitable (1 or 2 entries).
                If absolutely no item is suitable, reply with an empty array: []
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

    public static String buildOutfitImagePrompt(Gender gender, List<Suggestion> suggestions) {
        String mannequin = gender == Gender.MEN ? "male" : "female";

        String itemList = IntStream.range(0, suggestions.size())
                .mapToObj(i -> {
                    Suggestion s = suggestions.get(i);
                    String name = s.name() == null ? "item" : s.name();
                    return "  " + (i + 1) + ". Image " + (i + 2) + " - "
                            + s.category().name() + ": " + name;
                })
                .collect(Collectors.joining("\n"));

        return """
                You are compositing a virtual try-on image.

                Inputs:
                - Image 1: the %s mannequin reference. Use THIS exact mannequin
                  (same body shape, pose, framing, lighting) as the subject.
                - Images 2..%d: product photos of clothing items, in this order:
                %s

                Render ONE photorealistic image of the mannequin from Image 1
                wearing ALL of these exact garments together, layered correctly
                for the human body.

                HARD RULES (do not violate):
                - Do NOT invent, replace, or add any clothing item not present in the input garment images.
                - Do NOT alter the color, pattern, print, logo, fabric, cut, length, or
                  silhouette of any input garment. Preserve each garment's identity exactly.
                - Each garment in the output must be visually recognizable as the garment
                  from its corresponding input photo.
                - If two items occupy the same body region, layer them naturally (e.g. jacket
                  over shirt). Never omit an item.

                Scene:
                - Full-body, front-facing, neutral standing pose.
                - Plain neutral light-grey studio background.
                - Soft even studio lighting. No shadows on the background.
                - No text, no watermarks, no logos added, no collage, no multiple views.

                Output: a single composed image. No commentary.
                """.formatted(mannequin, suggestions.size() + 1, itemList);
    }
}
