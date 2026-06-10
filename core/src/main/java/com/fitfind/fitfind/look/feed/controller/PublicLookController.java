package com.fitfind.fitfind.look.feed.controller;

import com.fitfind.fitfind.look.common.model.response.LookDetailResponse;
import com.fitfind.fitfind.look.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/looks")
@RequiredArgsConstructor
public class PublicLookController {

    private final FeedService feedService;

    @GetMapping("/{lookId}")
    public ResponseEntity<LookDetailResponse> getLook(@PathVariable Long lookId) {
        return ResponseEntity.ok(feedService.lookDetails(lookId));
    }
}
