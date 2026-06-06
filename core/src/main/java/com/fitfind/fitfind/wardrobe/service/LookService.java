package com.fitfind.fitfind.wardrobe.service;

import com.fitfind.fitfind.wardrobe.model.Look;
import com.fitfind.fitfind.wardrobe.repository.LookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class LookService {
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
}
