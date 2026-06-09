package com.fitfind.fitfind.look.profile.controller;

import com.fitfind.fitfind.look.common.model.response.LookCardResponse;
import com.fitfind.fitfind.look.common.model.response.LooksResponse;
import com.fitfind.fitfind.look.profile.service.ProfileLookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/looks")
@RequiredArgsConstructor
public class ProfileLookController {

    private final ProfileLookService profileLookService;

    // todo: create request for it
    @PostMapping
    public ResponseEntity<Void> createLook(
        Authentication authentication
    ) {
        profileLookService.create(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<LooksResponse> getMyLooks(Authentication authentication) {
        return ResponseEntity.ok(profileLookService.list(authentication.getName()));
    }

    @GetMapping("/{lookId}")
    public ResponseEntity<LookCardResponse> getLook(
        Authentication authentication,
        @PathVariable Long lookId
    ) {
        return ResponseEntity.ok(profileLookService.get(authentication.getName(), lookId));
    }

    @PutMapping("/{lookId}/publish")
    public ResponseEntity<Void> publishLook(
        Authentication authentication,
        @PathVariable Long lookId
    ) {
        profileLookService.setPublished(authentication.getName(), lookId, true);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{lookId}/unpublish")
    public ResponseEntity<Void> unpublishLook(
        Authentication authentication,
        @PathVariable Long lookId
    ) {
        profileLookService.setPublished(authentication.getName(), lookId, false);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{lookId}")
    public ResponseEntity<Void> deleteLook(
        Authentication authentication,
        @PathVariable Long lookId
    ) {
        profileLookService.delete(authentication.getName(), lookId);
        return ResponseEntity.noContent().build();
    }
}
