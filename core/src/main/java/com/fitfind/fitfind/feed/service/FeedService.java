package com.fitfind.fitfind.feed.service;

import com.fitfind.fitfind.feed.repository.FeedRepository;
import com.fitfind.fitfind.wardrobe.model.Look;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;

    public Optional<List<Look>> getFeed() {
        return feedRepository.findLookByIsPublishedTrue(true);
    }
}
