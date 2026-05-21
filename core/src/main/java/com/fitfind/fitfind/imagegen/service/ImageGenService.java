package com.fitfind.fitfind.imagegen.service;

import com.fitfind.fitfind.ai.model.Suggestion;
import com.fitfind.fitfind.ai.model.enums.Gender;
import com.fitfind.fitfind.imagegen.config.ImageGenProperties;
import com.fitfind.fitfind.imagegen.exception.ImageGenerationException;
import com.fitfind.fitfind.imagegen.model.gemini.GeminiRequest;
import com.fitfind.fitfind.imagegen.model.gemini.GeminiResponse;
import com.fitfind.fitfind.imagegen.model.request.OutfitImageRequest;
import com.fitfind.fitfind.imagegen.model.response.OutfitImageResponse;
import com.fitfind.fitfind.security.ratelimit.model.RateLimitType;
import com.fitfind.fitfind.security.ratelimit.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

import static com.fitfind.fitfind.ai.utils.PromptHelper.buildOutfitImagePrompt;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageGenService {

    private static final String GENERATE_PATH = "/v1beta/models/{model}:generateContent";
    private static final String PARAM_KEY = "key";

    private final RestClient geminiRestClient;
    private final ImageGenProperties properties;
    private final ImageDownloader imageDownloader;
    private final MannequinProvider mannequinProvider;
    private final RateLimitService rateLimitService;

    public OutfitImageResponse generate(OutfitImageRequest request, String email) {
        rateLimitService.enforceRateLimit(email, RateLimitType.AI_GENERATION);

        ImageDownloader.DownloadedImage mannequin = mannequinProvider.forGender(request.gender())
                .orElseThrow(() -> new ImageGenerationException(
                        "No mannequin reference is configured for gender " + request.gender()
                                + ". Cannot generate outfit image."));

        List<Suggestion> withPictures = request.suggestions().stream()
                .filter(s -> s.picture() != null && !s.picture().isBlank())
                .toList();

        if (withPictures.isEmpty()) {
            throw new ImageGenerationException(
                    "No usable product pictures in suggestions to compose an outfit image.");
        }

        log.info("[imagegen] downloading {} product images", withPictures.size());
        List<ImageDownloader.DownloadedImage> downloaded = withPictures.stream()
                .map(s -> imageDownloader.download(s.picture()))
                .toList();

        GeminiRequest geminiRequest = buildGeminiRequest(request.gender(), withPictures, downloaded, mannequin);
        GeminiResponse geminiResponse = callGemini(geminiRequest);

        return extractImage(geminiResponse);
    }

    private GeminiRequest buildGeminiRequest(
            Gender gender,
            List<Suggestion> suggestions,
            List<ImageDownloader.DownloadedImage> images,
            ImageDownloader.DownloadedImage mannequin
    ) {
        List<GeminiRequest.Part> parts = new ArrayList<>();
        parts.add(GeminiRequest.Part.text(buildOutfitImagePrompt(gender, suggestions)));
        parts.add(GeminiRequest.Part.inline(mannequin.mimeType(), mannequin.base64()));
        for (ImageDownloader.DownloadedImage img : images) {
            parts.add(GeminiRequest.Part.inline(img.mimeType(), img.base64()));
        }

        return new GeminiRequest(
                List.of(new GeminiRequest.Content(parts)),
                new GeminiRequest.GenerationConfig(List.of("IMAGE"))
        );
    }

    private GeminiResponse callGemini(GeminiRequest request) {
        try {
            GeminiResponse response = geminiRestClient.post()
                    .uri(builder -> builder
                            .path(GENERATE_PATH)
                            .queryParam(PARAM_KEY, properties.getKey())
                            .build(properties.getModel()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new ImageGenerationException(
                                "Gemini request failed: " + res.getStatusCode());
                    })
                    .body(GeminiResponse.class);

            if (response == null) {
                throw new ImageGenerationException("Gemini returned empty response");
            }
            return response;
        } catch (ImageGenerationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ImageGenerationException("Gemini call failed", e);
        }
    }

    private OutfitImageResponse extractImage(GeminiResponse response) {
        if (response.promptFeedback() != null && response.promptFeedback().blockReason() != null) {
            throw new ImageGenerationException(
                    "Gemini blocked the request: " + response.promptFeedback().blockReason());
        }
        if (response.candidates() == null || response.candidates().isEmpty()) {
            throw new ImageGenerationException("Gemini returned no candidates");
        }

        GeminiResponse.Content content = response.candidates().getFirst().content();
        if (content == null || content.parts() == null) {
            throw new ImageGenerationException("Gemini candidate had no content parts");
        }

        for (GeminiResponse.Part part : content.parts()) {
            GeminiResponse.InlineData data = part.inlineData();
            if (data != null && data.data() != null && !data.data().isBlank()) {
                String mime = data.mimeType() == null ? "image/png" : data.mimeType();
                log.info("[imagegen] SUCCESS: received {} bytes (base64)", data.data().length());
                return new OutfitImageResponse(data.data(), mime, null);
            }
        }

        throw new ImageGenerationException("Gemini response contained no image data");
    }
}
