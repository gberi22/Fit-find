package com.fitfind.fitfind.ai.model.response;

import com.fitfind.fitfind.ai.model.Suggestion;

import java.util.List;

public record OutfitSuggestionResponse(
        List<Suggestion> suggestions
) { }
