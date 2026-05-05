package com.fitfind.fitfind.security.ratelimit.config;


import com.fitfind.fitfind.security.ratelimit.model.RateLimitType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitConfig {

    private Map<RateLimitType, LimitConfig> types = new EnumMap<>(RateLimitType.class);

    @Getter
    @Setter
    public static class LimitConfig {

        private Duration cooldown;
        private int capacity;
    }
}
