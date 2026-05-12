package com.fitfind.fitfind.websearch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "web-search-api")
public class WebSearchProperties {

    private String key;
    private String baseUrl = "https://serpapi.com";
}
