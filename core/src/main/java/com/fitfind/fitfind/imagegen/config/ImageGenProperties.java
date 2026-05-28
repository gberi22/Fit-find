package com.fitfind.fitfind.imagegen.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "gemini-api")
public class ImageGenProperties {

    private String key;
    private String baseUrl;
    private String model;
}
