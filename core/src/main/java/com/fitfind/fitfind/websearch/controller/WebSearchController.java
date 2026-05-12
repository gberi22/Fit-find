package com.fitfind.fitfind.websearch.controller;

import com.fitfind.fitfind.websearch.model.SearchedClothing;
import com.fitfind.fitfind.websearch.service.WebSearchService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shopping")
@RequiredArgsConstructor
public class WebSearchController {

    private final WebSearchService webSearchService;

    @GetMapping
    public List<SearchedClothing> search(
        Authentication authentication,
        @RequestParam @NotBlank String query
    ) {
        return webSearchService.searchGoogleShopping(query);
    }
}
