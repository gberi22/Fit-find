package com.fitfind.fitfind.websearch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "web-search-api")
public class WebSearchProperties {

    private String key;
    private String baseUrl;
    private Duration connectTimeout = Duration.ofSeconds(5);
    private Duration readTimeout = Duration.ofSeconds(10);
    private int maxAttempts = 3;
    private Duration retryBackoff = Duration.ofMillis(500);
}
