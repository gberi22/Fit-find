package com.fitfind.fitfind.look.common.service;

import com.fitfind.fitfind.look.common.exception.LookNotFoundException;
import com.fitfind.fitfind.look.common.model.Look;
import com.fitfind.fitfind.look.common.model.LookImage;
import com.fitfind.fitfind.look.common.repository.LookImageRepository;
import com.fitfind.fitfind.look.common.repository.LookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LookImageService {
    private final LookRepository lookRepository;
    private final LookImageRepository lookImageRepository;

    public Look lookByImageKey(UUID imageKey) {
        return lookRepository.findByImageKey(imageKey)
                .orElseThrow(() -> new LookNotFoundException("Look not found: " + imageKey));
    }

    public byte[] lookImage(Look look) {
        byte[] image = lookImageRepository.findById(look.getId())
                .map(LookImage::getImage)
                .orElse(null);
        if (image == null || image.length == 0) {
            throw new IllegalArgumentException("Look image not found.");
        }

        return image;
    }

    public MediaType lookContentType(Look look) {
        return look.getImageMimeType() == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(look.getImageMimeType());
    }
}
