package com.fitfind.fitfind.websearch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@ConfigurationProperties(prefix = "web-search-api")
@Getter
@Setter
public class WebSearchConfig {
    private String key;
    private String country = "ge";
    private String language = "ka";

    @Bean
    public RestClient webSearchRestClient() {
        return RestClient.create();
    }
}
