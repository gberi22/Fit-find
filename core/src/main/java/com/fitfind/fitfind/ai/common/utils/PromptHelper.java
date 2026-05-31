package com.fitfind.fitfind.ai.common.utils;

import com.fitfind.fitfind.ai.common.model.Suggestion;
import com.fitfind.fitfind.ai.common.model.enums.ClothingItem;
import com.fitfind.fitfind.ai.common.model.enums.Gender;
import com.fitfind.fitfind.ai.common.model.request.OutfitSuggestionRequest;
import com.fitfind.fitfind.ai.common.model.enums.Style;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PromptHelper {

    public static String styleAnalysisPrompt(String comments) {
        String userText = comments == null || comments.isBlank()
            ? "(none provided)"
            : comments;

        return """
                You are a fashion analyst. You are given one or more reference images of an
                outfit/fit, and optionally a short text description. Analyze the clothing.

                User's optional text description: %s

                First decide whether the image(s) are about clothing/fashion/fit at all. If they
                clearly do NOT depict clothing or a fashion look (e.g. a landscape, a meal, a
                document, a meme), set "fashionRelated" to false and leave "garments" empty.

                If it IS about fashion, list each distinct garment you see as a short
                natural-language description (e.g. "oversized beige wool trench coat",
                "white low-top sneakers"). Capture color, material, cut, and notable details
                so similar items can be found. Combine signals from BOTH the image(s) and the text.

                Reply with ONLY a single valid JSON object, no markdown, no commentary, exactly this shape:
                {
                  "fashionRelated": true,
                  "garments": ["light blue oxford shirt", "slim black tailored trousers"]
                }
                """.formatted(userText);
    }

    public static String formatGarmentDescriptions(List<String> garments, String comments) {
        String garmentText = garments == null ? "" : garments.stream()
            .filter(garment -> garment != null && !garment.isBlank())
            .collect(Collectors.joining("; "));

        StringBuilder stringBuilder = new StringBuilder();
        if (!garmentText.isBlank()) {
            stringBuilder.append("Reference fit details: ").append(garmentText).append('.');
        }
        if (comments != null && !comments.isBlank()) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append(' ');
            }
            stringBuilder.append("User notes: ").append(comments.trim());
        }
        return stringBuilder.toString();
    }

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
