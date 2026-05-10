package com.fitfind.fitfind.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai.github-models")
@Getter
@Setter
public class AIConfig {
    private String key;
    private String model;
    private String endpoint;
}
