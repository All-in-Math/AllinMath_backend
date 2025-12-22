package com.allinmath.backend.ratelimit;

import com.allinmath.backend.util.Logger;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing rate limits using Redis and Bucket4j
 * Implements token bucket algorithm for distributed rate limiting
 */
@Service
public class RateLimitService {

    private final RedisTemplate<String, Object> redisTemplate;
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

    public RateLimitService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Checks if a request is allowed based on rate limit
     * 
     * @param key Unique identifier for rate limiting (e.g., IP address or user ID)
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
     * @param key Unique identifier for rate limiting
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
            limit = Bandwidth.classic(
                sensitiveCapacity,
                Refill.intervally(sensitiveRefillTokens, Duration.ofMinutes(sensitiveRefillDurationMinutes))
            );
        } else {
            limit = Bandwidth.classic(
                defaultCapacity,
                Refill.intervally(defaultRefillTokens, Duration.ofMinutes(defaultRefillDurationMinutes))
            );
        }
        
        BucketConfiguration configuration = BucketConfiguration.builder()
            .addLimit(limit)
            .build();
            
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
