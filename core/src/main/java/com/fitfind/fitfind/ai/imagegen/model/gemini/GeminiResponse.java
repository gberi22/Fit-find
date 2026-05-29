package com.fitfind.fitfind.ai.imagegen.model.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiResponse(
        List<Candidate> candidates,
        PromptFeedback promptFeedback
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidate(Content content, String finishReason) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Content(List<Part> parts, String role) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Part(String text, InlineData inlineData) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record InlineData(String mimeType, String data) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PromptFeedback(String blockReason) { }
}
