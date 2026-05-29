package com.fitfind.fitfind.ai.imagegen.controller;

import com.fitfind.fitfind.ai.imagegen.model.request.OutfitImageRequest;
import com.fitfind.fitfind.ai.imagegen.model.response.OutfitImageResponse;
import com.fitfind.fitfind.ai.imagegen.service.ImageGenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class ImageGenController {

    private final ImageGenService imageGenService;

    @PostMapping("/outfit-image")
    public ResponseEntity<OutfitImageResponse> generate(
            Authentication authentication,
            @Valid @RequestBody OutfitImageRequest request
    ) {
        return ResponseEntity.ok(imageGenService.generate(request, authentication.getName()));
    }
}
