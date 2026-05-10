package com.fitfind.fitfind.websearch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "web-search-api")
@Getter
@Setter
public class WebSearchConfig {
    private String key;
}
