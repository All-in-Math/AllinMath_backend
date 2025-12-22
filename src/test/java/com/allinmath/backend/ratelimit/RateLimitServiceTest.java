package com.allinmath.backend.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RateLimitService
 */
@SpringBootTest
@TestPropertySource(properties = {
    "ratelimit.enabled=true",
    "ratelimit.default.capacity=5",
    "ratelimit.default.refill-tokens=5",
    "ratelimit.default.refill-duration-minutes=1",
    "ratelimit.sensitive.capacity=3",
    "ratelimit.sensitive.refill-tokens=3",
    "ratelimit.sensitive.refill-duration-minutes=1",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379"
})
class RateLimitServiceTest {

    @Autowired
    private RateLimitService rateLimitService;

    @Test
    void testDefaultRateLimitAllowsRequests() {
        String key = "test-user-1-" + System.currentTimeMillis();
        
        // Should allow up to capacity requests
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimitService.tryConsume(key, RateLimitType.DEFAULT),
                "Request " + (i + 1) + " should be allowed");
        }
        
        // Should deny the next request after exceeding capacity
        assertFalse(rateLimitService.tryConsume(key, RateLimitType.DEFAULT),
            "Request should be denied after exceeding rate limit");
    }

    @Test
    void testSensitiveRateLimitAllowsRequests() {
        String key = "test-user-2-" + System.currentTimeMillis();
        
        // Should allow up to capacity requests
        for (int i = 0; i < 3; i++) {
            assertTrue(rateLimitService.tryConsume(key, RateLimitType.SENSITIVE),
                "Request " + (i + 1) + " should be allowed");
        }
        
        // Should deny the next request after exceeding capacity
        assertFalse(rateLimitService.tryConsume(key, RateLimitType.SENSITIVE),
            "Request should be denied after exceeding rate limit");
    }

    @Test
    void testDifferentKeysHaveSeparateLimits() {
        String key1 = "test-user-3-" + System.currentTimeMillis();
        String key2 = "test-user-4-" + System.currentTimeMillis();
        
        // Consume all tokens for key1
        for (int i = 0; i < 5; i++) {
            rateLimitService.tryConsume(key1, RateLimitType.DEFAULT);
        }
        
        // key1 should be limited
        assertFalse(rateLimitService.tryConsume(key1, RateLimitType.DEFAULT));
        
        // key2 should still have tokens available
        assertTrue(rateLimitService.tryConsume(key2, RateLimitType.DEFAULT));
    }

    @Test
    void testGetAvailableTokens() {
        String key = "test-user-5-" + System.currentTimeMillis();
        
        // Initially should have full capacity
        long available = rateLimitService.getAvailableTokens(key, RateLimitType.DEFAULT);
        assertEquals(5, available, "Should have 5 tokens available initially");
        
        // Consume one token
        rateLimitService.tryConsume(key, RateLimitType.DEFAULT);
        
        // Should have one less token
        available = rateLimitService.getAvailableTokens(key, RateLimitType.DEFAULT);
        assertEquals(4, available, "Should have 4 tokens available after consuming one");
    }

    @Test
    void testResetRateLimit() {
        String key = "test-user-6-" + System.currentTimeMillis();
        
        // Consume all tokens
        for (int i = 0; i < 5; i++) {
            rateLimitService.tryConsume(key, RateLimitType.DEFAULT);
        }
        
        // Should be rate limited
        assertFalse(rateLimitService.tryConsume(key, RateLimitType.DEFAULT));
        
        // Reset the rate limit
        rateLimitService.resetRateLimit(key, RateLimitType.DEFAULT);
        
        // Should be able to make requests again
        assertTrue(rateLimitService.tryConsume(key, RateLimitType.DEFAULT));
    }
}
