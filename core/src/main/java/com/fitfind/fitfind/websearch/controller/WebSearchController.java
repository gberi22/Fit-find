package com.fitfind.fitfind.websearch.controller;

import com.fitfind.fitfind.websearch.model.SearchedClothing;
import com.fitfind.fitfind.websearch.service.WebSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/shopping")
@RequiredArgsConstructor
public class WebSearchController {

    private final WebSearchService webSearchService;

    @GetMapping("/search")
    public List<SearchedClothing> search(@RequestParam String query) {
        return webSearchService.searchGoogleShopping(query);
    }
}
