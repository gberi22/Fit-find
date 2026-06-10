package com.fitfind.fitfind.look.common.model.response;

import com.fitfind.fitfind.ai.common.model.enums.ClothingItem;

public record ProductResponse(
    String name,
    String url,
    String price,
    ClothingItem category,
    String imageUrl
) { }
