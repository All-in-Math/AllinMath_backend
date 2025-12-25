package com.allinmath.backend.ratelimit;

import com.allinmath.backend.util.Logger;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing rate limits using Bucket4j
 * Implements token bucket algorithm for rate limiting
 * 
 * Note: This implementation uses in-memory storage (ConcurrentHashMap).
 * For true distributed rate limiting across multiple server instances,
 * integrate Bucket4j with Redis using bucket4j-redis module.
 */
@Service
public class RateLimitService {

    private final ConcurrentHashMap<String, Bucket> localBuckets = new ConcurrentHashMap<>();

    @Value("${ratelimit.enabled}")
    private boolean rateLimitEnabled;

    @Value("${ratelimit.default.capacity}")
    private long defaultCapacity;

    @Value("${ratelimit.default.refill-tokens}")
    private long defaultRefillTokens;

    @Value("${ratelimit.default.refill-duration-minutes}")
    private long defaultRefillDurationMinutes;

    @Value("${ratelimit.sensitive.capacity}")
    private long sensitiveCapacity;

    @Value("${ratelimit.sensitive.refill-tokens}")
    private long sensitiveRefillTokens;

    @Value("${ratelimit.sensitive.refill-duration-minutes}")
    private long sensitiveRefillDurationMinutes;

    /**
     * Checks if a request is allowed based on rate limit
     * 
     * @param key  Unique identifier for rate limiting (e.g., IP address or user ID)
     * @param type Rate limit type (DEFAULT or SENSITIVE)
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean tryConsume(String key, RateLimitType type) {
        if (!rateLimitEnabled) {
            return true;
        }

        String rateLimitKey = "ratelimit:" + type.name().toLowerCase() + ":" + key;
        Bucket bucket = localBuckets.computeIfAbsent(rateLimitKey, k -> createBucket(type));

        boolean consumed = bucket.tryConsume(1);

        if (!consumed) {
            Logger.w("Rate limit exceeded for key: " + key + ", type: " + type);
        }

        return consumed;
    }

    /**
     * Gets remaining tokens for a key
     * 
     * @param key  Unique identifier for rate limiting
     * @param type Rate limit type
     * @return Number of available tokens
     */
    public long getAvailableTokens(String key, RateLimitType type) {
        if (!rateLimitEnabled) {
            return Long.MAX_VALUE;
        }

        String rateLimitKey = "ratelimit:" + type.name().toLowerCase() + ":" + key;
        Bucket bucket = localBuckets.computeIfAbsent(rateLimitKey, k -> createBucket(type));
        return bucket.getAvailableTokens();
    }

    /**
     * Creates a new bucket with the specified configuration
     */
    private Bucket createBucket(RateLimitType type) {
        Bandwidth limit;

        if (type == RateLimitType.SENSITIVE) {
            limit = Bandwidth.builder()
                    .capacity(sensitiveCapacity)
                    .refillIntervally(sensitiveRefillTokens, Duration.ofMinutes(sensitiveRefillDurationMinutes))
                    .build();
        } else {
            limit = Bandwidth.builder()
                    .capacity(defaultCapacity)
                    .refillIntervally(defaultRefillTokens, Duration.ofMinutes(defaultRefillDurationMinutes))
                    .build();
        }

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Resets rate limit for a specific key (admin use)
     */
    public void resetRateLimit(String key, RateLimitType type) {
        String rateLimitKey = "ratelimit:" + type.name().toLowerCase() + ":" + key;
        localBuckets.remove(rateLimitKey);
        Logger.i("Rate limit reset for key: " + key + ", type: " + type);
    }
}
