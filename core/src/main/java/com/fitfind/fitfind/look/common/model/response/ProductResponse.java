package com.fitfind.fitfind.look.common.model.response;

import com.fitfind.fitfind.ai.common.model.enums.ClothingItem;

public record ProductResponse(
    Long id,
    String name,
    String price,
    String url,
    ClothingItem category,
    String storeName
) { }
