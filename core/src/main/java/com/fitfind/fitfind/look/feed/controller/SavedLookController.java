package com.fitfind.fitfind.look.feed.controller;

import com.fitfind.fitfind.look.feed.service.SavedLookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/looks")
@RequiredArgsConstructor
public class SavedLookController {
    private final SavedLookService savedLookService;

    @PostMapping("/{lookId}")
    public ResponseEntity<Void> save(Authentication authentication, @PathVariable Long lookId) {
        savedLookService.save(authentication.getName(), lookId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{lookId}")
    public ResponseEntity<Void> unsave(Authentication authentication, @PathVariable Long lookId) {
        savedLookService.unsave(authentication.getName(), lookId);
        return ResponseEntity.noContent().build();
    }
}
