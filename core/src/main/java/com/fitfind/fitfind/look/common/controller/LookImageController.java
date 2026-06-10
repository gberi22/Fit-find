package com.fitfind.fitfind.look.common.controller;

import com.fitfind.fitfind.look.common.model.Look;
import com.fitfind.fitfind.look.common.service.LookImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/look-images")
@RequiredArgsConstructor
public class LookImageController {

    private final LookImageService lookImageService;

    @GetMapping("/{imageKey}")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> lookImage(@PathVariable UUID imageKey) {
        Look look = lookImageService.lookByImageKey(imageKey);
        byte[] image = lookImageService.lookImage(look);
        MediaType contentType = lookImageService.lookContentType(look);

        return ResponseEntity.ok().contentType(contentType).body(image);
    }
}
