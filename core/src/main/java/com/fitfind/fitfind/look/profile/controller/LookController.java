package com.fitfind.fitfind.look.profile.controller;

import com.fitfind.fitfind.look.common.model.response.LookResponse;
import com.fitfind.fitfind.look.common.model.response.LooksPageResponse;
import com.fitfind.fitfind.look.profile.service.LookService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/looks")
@RequiredArgsConstructor
public class LookController {

    private final LookService lookService;

    // todo: create request for it
    @PostMapping
    public ResponseEntity<Void> createLook(
        Authentication authentication
    ) {
        lookService.create(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<LooksPageResponse> getMyLooks(
        Authentication authentication,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return ResponseEntity.ok(lookService.list(authentication.getName(), page, size));
    }

    @GetMapping("/{lookId}")
    public ResponseEntity<LookResponse> getLook(
        Authentication authentication,
        @PathVariable Long lookId
    ) {
        return ResponseEntity.ok(lookService.get(authentication.getName(), lookId));
    }

    @PutMapping("/{lookId}/publish")
    public ResponseEntity<Void> publishLook(
        Authentication authentication,
        @PathVariable Long lookId
    ) {
        lookService.setPublished(authentication.getName(), lookId, true);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{lookId}/unpublish")
    public ResponseEntity<Void> unpublishLook(
        Authentication authentication,
        @PathVariable Long lookId
    ) {
        lookService.setPublished(authentication.getName(), lookId, false);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{lookId}")
    public ResponseEntity<Void> deleteLook(
        Authentication authentication,
        @PathVariable Long lookId
    ) {
        lookService.delete(authentication.getName(), lookId);
        return ResponseEntity.noContent().build();
    }
}
