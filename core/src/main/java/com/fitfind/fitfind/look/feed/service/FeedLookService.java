package com.fitfind.fitfind.look.feed.service;

import com.fitfind.fitfind.look.common.model.Look;
import com.fitfind.fitfind.look.common.repository.LookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedLookService {
    private final LookRepository lookRepository;

    public Look lookById(Long id) {
        return lookRepository.findByIdAndIsPublishedTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Look not published."));
    }
}
