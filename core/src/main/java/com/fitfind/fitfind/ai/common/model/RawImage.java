package com.fitfind.fitfind.ai.common.model;

public record RawImage(
    byte[] bytes,
    String mimeType
) { }
