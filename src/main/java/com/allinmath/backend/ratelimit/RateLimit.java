package com.allinmath.backend.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to apply rate limiting to controller methods
 * Uses Redis-backed token bucket algorithm for distributed rate limiting
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    /**
     * Rate limit type
     * SENSITIVE: Lower limits for security-critical endpoints (e.g., register, password reset)
     * DEFAULT: Standard limits for regular authenticated endpoints
     */
    RateLimitType type() default RateLimitType.DEFAULT;
    
    /**
     * Custom rate limit key
     * If not specified, uses IP address for unauthenticated endpoints or user ID for authenticated endpoints
     */
    String key() default "";
}
