package com.fitfind.fitfind.imagegen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ImageGenConfig {

    @Bean
    public RestClient geminiRestClient(ImageGenProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    @Bean
    public RestClient imageDownloadRestClient() {
        return RestClient.builder().build();
    }
}
