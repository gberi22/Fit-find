package com.fitfind.fitfind.ai.imagegen.model.response;

public record OutfitImageResponse(
        String imageBase64,
        String mimeType,
        String message
) { }
