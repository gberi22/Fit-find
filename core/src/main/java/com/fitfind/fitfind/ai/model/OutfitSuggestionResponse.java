package com.fitfind.fitfind.ai.model;

import java.util.List;

public record OutfitSuggestionResponse(
        List<Suggestion> suggestions
) { }
