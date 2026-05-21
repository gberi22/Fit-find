package com.fitfind.fitfind.imagegen.model.gemini;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeminiRequest(
        List<Content> contents,
        GenerationConfig generationConfig
) {
    public record Content(List<Part> parts) { }

    public record Part(String text, InlineData inlineData) {
        public static Part text(String text) {
            return new Part(text, null);
        }

        public static Part inline(String mimeType, String base64) {
            return new Part(null, new InlineData(mimeType, base64));
        }
    }

    public record InlineData(String mimeType, String data) { }

    public record GenerationConfig(List<String> responseModalities) { }
}
