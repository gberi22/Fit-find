package com.fitfind.fitfind.feed.service;

import com.fitfind.fitfind.feed.model.GeneralFeedResponse;
import com.fitfind.fitfind.feed.repository.FeedRepository;
import com.fitfind.fitfind.wardrobe.model.Look;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;

    public GeneralFeedResponse list(boolean isPublished, int page, int size) {
        Pageable pageable = PageRequest.of(page, size)
                .withSort(Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Look> result = feedRepository.findLookByIsPublishedTrue(isPublished, pageable);
        List<GeneralFeedResponse.LookItem> items = result.getContent().stream()
                .map(look -> new GeneralFeedResponse.LookItem(
                        look.getId(),
                        look.getCreatedAt()
                ))
                .toList();
        return new GeneralFeedResponse(items, result.getTotalElements(), result.getTotalPages());
    }
}
