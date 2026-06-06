package com.fitfind.fitfind.wardrobe.controller;

import com.fitfind.fitfind.wardrobe.model.Look;
import com.fitfind.fitfind.wardrobe.service.LookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/looks")
@RequiredArgsConstructor
public class LookController {
    private final LookService lookService;

    @GetMapping("/{id}/image")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> lookImage(@PathVariable Long id) {
        Look look = lookService.lookById(id);
        byte[] image = lookService.lookImage(look);
        MediaType contentType = look.getImageMimeType() == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(look.getImageMimeType());

        return ResponseEntity.ok().contentType(contentType).body(image);
    }
}
