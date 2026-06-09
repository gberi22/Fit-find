package com.fitfind.fitfind.look.feed.service;

import com.fitfind.fitfind.look.common.model.Look;
import com.fitfind.fitfind.look.common.repository.LookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FeedLookService {
    private final LookRepository lookRepository;

    public Look lookById(Long id) {
        return lookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    public byte[] lookImage(Look look) {
        byte[] image = look.getImage();
        if (image == null || image.length == 0) {
            throw new ResponseStatusException(NOT_FOUND);
        }

        return image;
    }

    public MediaType lookContentType(Look look) {
        return look.getImageMimeType() == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(look.getImageMimeType());
    }
}
