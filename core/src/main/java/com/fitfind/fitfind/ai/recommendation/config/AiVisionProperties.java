package com.fitfind.fitfind.ai.recommendation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@Getter
@Setter
@ConfigurationProperties(prefix = "ai.vision")
public class AiVisionProperties {

    private int maxImages;
    private DataSize maxImageSize;
}
