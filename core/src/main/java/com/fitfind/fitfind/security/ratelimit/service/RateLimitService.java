package com.fitfind.fitfind.security.ratelimit.service;

import com.fitfind.fitfind.security.ratelimit.config.RateLimitConfig;
import com.fitfind.fitfind.security.ratelimit.config.RateLimitConfig.LimitConfig;
import com.fitfind.fitfind.security.ratelimit.exception.TooManyRequestException;
import com.fitfind.fitfind.security.ratelimit.model.RateLimitType;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final RateLimitConfig rateLimitProperties;

    public void enforceRateLimit(String identifier, RateLimitType type) {
        String key = identifier + ": " + type;
        LimitConfig config = rateLimitProperties.getTypes().get(type);
        buckets.computeIfAbsent(key, k -> newBucket(config.getCooldown(), config.getCapacity()));
        if (!buckets.get(key).tryConsume(1)) {
            throw new TooManyRequestException("Too many requests sent");
        }
    }

    private Bucket newBucket(Duration cooldown, int capacity) {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(capacity)
                        .refillIntervally(capacity, cooldown)
                        .build())
                .build();
    }
}
