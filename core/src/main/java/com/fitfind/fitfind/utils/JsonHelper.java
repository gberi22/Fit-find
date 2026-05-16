package com.fitfind.fitfind.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JsonHelper {

    public static JsonNode parseJsonObject(String response, ObjectMapper objectMapper) {
        if (response == null) {
            return null;
        }
        String cleaned = response.trim();
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start < 0 || end <= start) {
            return null;
        }
        try {
            return objectMapper.readTree(cleaned.substring(start, end + 1));
        } catch (Exception e) {
            log.warn("Failed to parse AI JSON response: {}", e.getMessage());
            return null;
        }
    }

    public static String textOrNull(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        String text = node.asText();
        return text.isBlank() ? null : text;
    }
}
