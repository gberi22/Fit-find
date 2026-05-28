package com.fitfind.fitfind.ai.common.model.response;

import com.fitfind.fitfind.ai.common.model.CategorySuggestions;

import java.util.List;

public record OutfitSuggestionResponse(
        List<CategorySuggestions> categories
) { }
