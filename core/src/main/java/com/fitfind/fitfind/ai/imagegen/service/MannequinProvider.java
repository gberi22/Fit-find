package com.fitfind.fitfind.ai.imagegen.service;

import com.fitfind.fitfind.ai.common.model.enums.Gender;
import com.fitfind.fitfind.ai.imagegen.model.InlineImage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class MannequinProvider {

    private static final String BASE_PATH = "imagegen/mannequins/";
    private static final List<String> EXTENSIONS = List.of("png", "jpg", "jpeg");

    private final Map<Gender, InlineImage> cache = new EnumMap<>(Gender.class);

    @PostConstruct
    void load() {
        loadFromClasspath(Gender.MEN, "men");
        loadFromClasspath(Gender.WOMEN, "women");
    }

    public Optional<InlineImage> forGender(Gender gender) {
        return Optional.ofNullable(cache.get(gender));
    }

    private void loadFromClasspath(Gender gender, String baseName) {
        boolean loaded = EXTENSIONS.stream()
            .anyMatch(ext -> tryLoadMannequin(gender, baseName, ext));
        if (!loaded) {
            log.warn("[imagegen] no mannequin reference found for {} (looked for {}{}.[png|jpg|jpeg]) "
                + "- image generation will be REJECTED until one is provided",
                gender, BASE_PATH, baseName);
        }
    }

    private boolean tryLoadMannequin(Gender gender, String baseName, String ext) {
        String path = BASE_PATH + baseName + "." + ext;
        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            return false;
        }
        try (InputStream in = resource.getInputStream()) {
            byte[] bytes = in.readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String mime = "jpg".equals(ext) ? "image/jpeg" : "image/" + ext;
            cache.put(gender, new InlineImage(mime, base64));
            log.info("[imagegen] loaded mannequin reference for {} from {} ({} bytes)",
                    gender, path, bytes.length);
            return true;
        } catch (IOException e) {
            log.warn("[imagegen] failed to read {}: {}", path, e.getMessage());
            return false;
        }
    }
}
