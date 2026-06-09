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

    @PostMapping("/{id}/save")
    public ResponseEntity<Void> save(Authentication authentication, @PathVariable Long id) {
        savedLookService.save(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/save")
    public ResponseEntity<Void> unsave(Authentication authentication, @PathVariable Long id) {
        savedLookService.unsave(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
