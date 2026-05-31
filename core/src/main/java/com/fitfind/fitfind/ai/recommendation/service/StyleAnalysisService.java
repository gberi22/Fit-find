package com.fitfind.fitfind.ai.recommendation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfind.fitfind.ai.common.model.RawImage;
import com.fitfind.fitfind.ai.recommendation.exception.InvalidReferenceImageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.util.ArrayList;
import java.util.List;

import static com.fitfind.fitfind.ai.common.utils.JsonHelper.parseJsonObject;
import static com.fitfind.fitfind.ai.common.utils.JsonHelper.textOrNull;
import static com.fitfind.fitfind.ai.common.utils.PromptHelper.styleAnalysisPrompt;

@Service
@Slf4j
@RequiredArgsConstructor
public class StyleAnalysisService {

    private final ChatClient visionChatClient;
    private final ObjectMapper objectMapper;

    public List<String> analyze(List<RawImage> images, String comments) {
        String response = call(images, comments);
        log.info("[style-analysis] raw response: {}", response);

        JsonNode root = parseJsonObject(response, objectMapper);
        if (root == null) {
            throw new InvalidReferenceImageException(
                "Could not analyze the reference input. Please try a clearer photo or description."
            );
        }

        boolean fashionRelated = root.path("fashionRelated").asBoolean(false);
        List<String> garments = mapStrings(root.get("garments"));

        if (!fashionRelated || garments.isEmpty()) {
            throw new InvalidReferenceImageException(
                "The reference doesn't look like a clothing/fashion item. " +
                    "Please upload a photo of an outfit or describe the fit you want."
            );
        }

        return garments;
    }

    private String call(List<RawImage> images, String comments) {
        try {
            return visionChatClient.prompt()
                .user(u -> {
                    u.text(styleAnalysisPrompt(comments));
                    if (images != null) {
                        images.forEach(
                            img -> u.media(mimeType(img.mimeType()),
                            new ByteArrayResource(img.bytes()))
                        );
                    }
                })
                .call()
                .content();
        } catch (RuntimeException e) {
            log.warn("[style-analysis] vision call failed: {}", e.getMessage(), e);
            throw new InvalidReferenceImageException("Failed to analyze the reference input. Please try again.");
        }
    }

    private MimeType mimeType(String raw) {
        try {
            return MimeTypeUtils.parseMimeType(raw);
        } catch (RuntimeException e) {
            return MimeTypeUtils.IMAGE_JPEG;
        }
    }

    private List<String> mapStrings(JsonNode array) {
        if (array == null || !array.isArray()) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        array.forEach(node -> {
            String text = textOrNull(node);
            if (text != null) {
                values.add(text.trim());
            }
        });
        return values;
    }
}
