package com.fitfind.fitfind.ai.imagegen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class ImageGenConfig {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration GEMINI_READ_TIMEOUT = Duration.ofSeconds(60);
    private static final Duration DOWNLOAD_READ_TIMEOUT = Duration.ofSeconds(15);
    private static final String API_KEY_HEADER = "x-goog-api-key";

    @Bean
    public RestClient geminiRestClient(ImageGenProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(CONNECT_TIMEOUT)
            .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(GEMINI_READ_TIMEOUT);

        return RestClient.builder()
            .requestFactory(factory)
            .baseUrl(properties.getBaseUrl())
            .defaultHeader(API_KEY_HEADER, properties.getKey())
            .build();
    }

    @Bean
    public RestClient imageDownloadRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(CONNECT_TIMEOUT)
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(DOWNLOAD_READ_TIMEOUT);

        return RestClient.builder()
            .requestFactory(factory)
            .build();
    }
}
