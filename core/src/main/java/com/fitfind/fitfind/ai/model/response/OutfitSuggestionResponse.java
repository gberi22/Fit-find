package com.fitfind.fitfind.ai.model.response;

import com.fitfind.fitfind.ai.model.CategorySuggestions;

import java.util.List;

public record OutfitSuggestionResponse(
        List<CategorySuggestions> categories
) { }
