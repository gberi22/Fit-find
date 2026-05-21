package com.fitfind.fitfind.imagegen.service;

import com.fitfind.fitfind.imagegen.exception.ImageGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImageDownloader {

    private static final String FALLBACK_MIME = "image/jpeg";

    private final RestClient imageDownloadRestClient;

    public DownloadedImage download(String url) {
        try {
            ResponseEntity<byte[]> response = imageDownloadRestClient.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(byte[].class);

            byte[] bytes = response.getBody();
            if (bytes == null || bytes.length == 0) {
                throw new ImageGenerationException("Empty body when downloading " + url);
            }

            MediaType contentType = response.getHeaders().getContentType();
            String mime = contentType == null ? FALLBACK_MIME : contentType.toString();
            if (!mime.startsWith("image/")) {
                mime = FALLBACK_MIME;
            }

            return new DownloadedImage(mime, Base64.getEncoder().encodeToString(bytes));
        } catch (ImageGenerationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ImageGenerationException("Failed to download product image: " + url, e);
        }
    }

    public record DownloadedImage(String mimeType, String base64) { }
}
