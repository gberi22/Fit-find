package com.fitfind.fitfind.look.feed.controller;

import com.fitfind.fitfind.look.feed.model.FeedRequestFilters;
import com.fitfind.fitfind.look.feed.model.FeedResponse;
import com.fitfind.fitfind.look.feed.service.FeedService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<FeedResponse> generalFeed(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
            @ModelAttribute FeedRequestFilters requestFilters
    ) {
        return ResponseEntity.ok(
                feedService.list(requestFilters, page, size));
    }
}
