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
    private Duration connectTimeout;
    private Duration readTimeout;
    private int maxAttempts;
    private Duration retryBackoff;
}
