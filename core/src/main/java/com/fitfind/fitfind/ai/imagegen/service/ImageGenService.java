package com.fitfind.fitfind.ai.imagegen.service;

import com.fitfind.fitfind.ai.common.model.Suggestion;
import com.fitfind.fitfind.ai.common.model.enums.Gender;
import com.fitfind.fitfind.ai.imagegen.config.ImageGenProperties;
import com.fitfind.fitfind.ai.imagegen.exception.ImageGenerationException;
import com.fitfind.fitfind.ai.imagegen.model.InlineImage;
import com.fitfind.fitfind.ai.imagegen.model.gemini.GeminiRequest;
import com.fitfind.fitfind.ai.imagegen.model.gemini.GeminiResponse;
import com.fitfind.fitfind.ai.imagegen.model.request.OutfitImageRequest;
import com.fitfind.fitfind.ai.imagegen.model.response.OutfitImageResponse;
import com.fitfind.fitfind.security.ratelimit.model.RateLimitType;
import com.fitfind.fitfind.security.ratelimit.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import static com.fitfind.fitfind.ai.common.utils.PromptHelper.buildOutfitImagePrompt;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageGenService {

    private static final String GENERATE_PATH = "/v1beta/models/{model}:generateContent";
    private static final String FALLBACK_IMAGE_MIME = "image/jpeg";
    private static final int MAX_IMAGE_BYTES = 10 * 1024 * 1024;

    private final RestClient geminiRestClient;
    private final RestClient imageDownloadRestClient;
    private final ImageGenProperties properties;
    private final MannequinProvider mannequinProvider;
    private final RateLimitService rateLimitService;

    public OutfitImageResponse generate(OutfitImageRequest request, String email) {
        rateLimitService.enforceRateLimit(email, RateLimitType.IMAGE_GENERATION);

        InlineImage mannequin = mannequinProvider.forGender(request.gender())
            .orElseThrow(() -> new ImageGenerationException(
                "No mannequin reference is configured for gender " + request.gender()
                    + ". Cannot generate outfit image."));

        List<Suggestion> suggestionsWithPictures = request.suggestions().stream()
            .filter(suggestion -> suggestion.picture() != null && !suggestion.picture().isBlank())
            .toList();

        if (suggestionsWithPictures.isEmpty()) {
            throw new ImageGenerationException("No usable product pictures in suggestions to compose an outfit image.");
        }

        log.info("[imagegen] downloading {} product images", suggestionsWithPictures.size());
        List<InlineImage> garments = suggestionsWithPictures.stream()
            .map(suggestion -> downloadProductImage(suggestion.picture()))
            .toList();

        GeminiRequest geminiRequest = buildGeminiRequest(request.gender(), suggestionsWithPictures, garments, mannequin);
        GeminiResponse geminiResponse = callGemini(geminiRequest);

        return extractImage(geminiResponse);
    }

    private InlineImage downloadProductImage(String url) {
        validatePublicImageUrl(url);
        try {
            ResponseEntity<byte[]> response = imageDownloadRestClient.get()
                .uri(url)
                .retrieve()
                .toEntity(byte[].class);

            byte[] bytes = response.getBody();
            String mimeType = resolveImageMimeType(url, bytes, response);

            return new InlineImage(mimeType, Base64.getEncoder().encodeToString(bytes));
        } catch (ImageGenerationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ImageGenerationException("Failed to download product image: " + url, e);
        }
    }

    private String resolveImageMimeType(String url, byte[] bytes, ResponseEntity<byte[]> response) {
        if (bytes == null || bytes.length == 0) {
            throw new ImageGenerationException("Empty body when downloading " + url);
        }
        if (bytes.length > MAX_IMAGE_BYTES) {
            throw new ImageGenerationException("Product image exceeds size limit: " + url);
        }

        MediaType contentType = response.getHeaders().getContentType();
        String mimeType = contentType == null ? FALLBACK_IMAGE_MIME : contentType.toString();
        if (!mimeType.startsWith("image/")) {
            mimeType = FALLBACK_IMAGE_MIME;
        }
        return mimeType;
    }

    private void validatePublicImageUrl(String url) {
        URI uri = parseUri(url);
        requireHttpScheme(uri, url);
        String host = requireHost(uri, url);
        requirePublicHost(host);
    }

    private URI parseUri(String url) {
        try {
            return URI.create(url);
        } catch (IllegalArgumentException e) {
            throw new ImageGenerationException("Invalid product image URL: " + url, e);
        }
    }

    private void requireHttpScheme(URI uri, String url) {
        String scheme = uri.getScheme();
        if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new ImageGenerationException("Refusing to download non-http(s) URL: " + url);
        }
    }

    private String requireHost(URI uri, String url) {
        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new ImageGenerationException("Product image URL has no host: " + url);
        }
        return host;
    }

    private void requirePublicHost(String host) {
        InetAddress[] addresses;
        try {
            addresses = InetAddress.getAllByName(host);
        } catch (UnknownHostException e) {
            throw new ImageGenerationException("Cannot resolve product image host: " + host, e);
        }

        if (Arrays.stream(addresses).anyMatch(ImageGenService::isNonPublicAddress)) {
            throw new ImageGenerationException("Refusing to download from a non-public address for host: " + host);
        }
    }

    private static boolean isNonPublicAddress(InetAddress address) {
        return address.isLoopbackAddress() || address.isAnyLocalAddress()
            || address.isLinkLocalAddress() || address.isSiteLocalAddress()
            || address.isMulticastAddress();
    }

    private GeminiRequest buildGeminiRequest(
        Gender gender,
        List<Suggestion> suggestions,
        List<InlineImage> garments,
        InlineImage mannequin
    ) {
        List<GeminiRequest.Part> parts = Stream.concat(
            Stream.of(
                GeminiRequest.Part.text(buildOutfitImagePrompt(gender, suggestions)),
                GeminiRequest.Part.inline(mannequin.mimeType(), mannequin.base64())
            ),
            garments.stream()
                .map(garment -> GeminiRequest.Part.inline(garment.mimeType(), garment.base64()))
        ).toList();

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

        return content.parts().stream()
            .map(GeminiResponse.Part::inlineData)
            .filter(inlineData -> inlineData != null && inlineData.data() != null && !inlineData.data().isBlank())
            .findFirst()
            .map(inlineData -> {
                String mimeType = inlineData.mimeType() == null ? "image/png" : inlineData.mimeType();
                log.info("[imagegen] SUCCESS: received {} bytes (base64)", inlineData.data().length());
                return new OutfitImageResponse(inlineData.data(), mimeType, null);
            })
            .orElseThrow(() -> new ImageGenerationException("Gemini response contained no image data"));
    }
}
