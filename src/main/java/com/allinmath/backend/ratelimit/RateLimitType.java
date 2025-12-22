package com.allinmath.backend.ratelimit;

/**
 * Types of rate limits
 */
public enum RateLimitType {
    /**
     * Default rate limit for regular endpoints
     * Higher capacity, suitable for general API usage
     */
    DEFAULT,
    
    /**
     * Sensitive rate limit for security-critical endpoints
     * Lower capacity to prevent abuse (registration, password reset, email verification)
     */
    SENSITIVE
}
