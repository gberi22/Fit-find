package com.fitfind.fitfind.imagegen.model.response;

public record OutfitImageResponse(
        String imageBase64,
        String mimeType,
        String message
) { }
