package com.allinmath.backend.endpoint;

import com.allinmath.backend.ratelimit.RateLimitService;
import com.allinmath.backend.ratelimit.RateLimitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for rate limiting on endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "ratelimit.enabled=true",
    "ratelimit.sensitive.capacity=3",
    "ratelimit.sensitive.refill-tokens=3",
    "ratelimit.sensitive.refill-duration-minutes=1"
})
class RateLimitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        // Reset rate limits before each test
        rateLimitService.resetRateLimit("ip:127.0.0.1", RateLimitType.SENSITIVE);
    }

    @Test
    void testPasswordResetRateLimiting() throws Exception {
        String requestBody = "{\"email\": \"test@example.com\"}";

        // First 3 requests should succeed (within rate limit)
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/account/password/reset")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk());
        }

        // 4th request should be rate limited (429 Too Many Requests)
        mockMvc.perform(post("/account/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void testRegisterRateLimiting() throws Exception {
        String requestBody = "{\"email\": \"newuser@example.com\", \"password\": \"SecurePass123!\", \"firstName\": \"Test\", \"lastName\": \"User\", \"role\": \"STUDENT\"}";

        // First 3 requests should succeed (within rate limit)
        // Note: These will fail for other reasons (validation, Firebase, etc.)
        // but should not be blocked by rate limiting
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/account/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // We don't check status here because the request may fail for other reasons
        }

        // After consuming rate limit, should get 429
        mockMvc.perform(post("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isTooManyRequests());
    }
}
