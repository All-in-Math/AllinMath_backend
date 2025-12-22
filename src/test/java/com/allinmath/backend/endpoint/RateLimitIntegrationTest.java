package com.allinmath.backend.endpoint;

import com.allinmath.backend.ratelimit.RateLimitService;
import com.allinmath.backend.ratelimit.RateLimitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for rate limiting service
 * Tests core rate limiting functionality without HTTP layer
 */
@SpringBootTest
@TestPropertySource(properties = {
    "ratelimit.enabled=true",
    "ratelimit.sensitive.capacity=3",
    "ratelimit.sensitive.refill-tokens=3",
    "ratelimit.sensitive.refill-duration-minutes=1",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379"
})
class RateLimitIntegrationTest {

    @Autowired
    private RateLimitService rateLimitService;

    @Test
    void testRateLimitServiceIsConfigured() {
        assertNotNull(rateLimitService, "RateLimitService should be autowired");
    }

    @Test
    void testSensitiveEndpointRateLimitingBehavior() {
        String testKey = "integration-test-" + System.currentTimeMillis();
        
        // Simulate 3 requests (within limit)
        for (int i = 0; i < 3; i++) {
            assertTrue(rateLimitService.tryConsume(testKey, RateLimitType.SENSITIVE),
                "Request " + (i + 1) + " should be allowed");
        }

        // 4th request should be rate limited
        assertFalse(rateLimitService.tryConsume(testKey, RateLimitType.SENSITIVE),
            "Request should be denied after exceeding rate limit");
    }
    
    @Test
    void testRateLimitingIsIndependentPerKey() {
        String key1 = "integration-test-key1-" + System.currentTimeMillis();
        String key2 = "integration-test-key2-" + System.currentTimeMillis();
        
        // Exhaust key1's rate limit
        for (int i = 0; i < 3; i++) {
            rateLimitService.tryConsume(key1, RateLimitType.SENSITIVE);
        }
        
        // key1 should be rate limited
        assertFalse(rateLimitService.tryConsume(key1, RateLimitType.SENSITIVE),
            "key1 should be rate limited");
        
        // key2 should still have capacity
        assertTrue(rateLimitService.tryConsume(key2, RateLimitType.SENSITIVE),
            "key2 should not be rate limited");
    }
}
