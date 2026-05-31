package com.fitfind.fitfind.ai.recommendation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ai.github-models")
public class AiRecommendationProperties {

    private String key;
    private String model;
    private String endpoint;
    private String visionModel;
}
