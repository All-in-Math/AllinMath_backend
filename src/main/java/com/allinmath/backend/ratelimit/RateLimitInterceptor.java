package com.allinmath.backend.ratelimit;

import com.allinmath.backend.exception.RateLimitExceededException;
import com.allinmath.backend.util.Logger;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that applies rate limiting to controller methods annotated with @RateLimit
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    public RateLimitInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);

        if (rateLimit == null) {
            return true;
        }

        String key = getRateLimitKey(request, rateLimit);
        RateLimitType type = rateLimit.type();

        boolean allowed = rateLimitService.tryConsume(key, type);

        if (!allowed) {
            long availableTokens = rateLimitService.getAvailableTokens(key, type);
            Logger.w("Rate limit exceeded for key: " + key + " on endpoint: " + request.getRequestURI());
            throw new RateLimitExceededException(
                "Rate limit exceeded. Please try again later. Available tokens: " + availableTokens
            );
        }

        return true;
    }

    /**
     * Determines the rate limit key based on authentication status
     * For authenticated users: uses Firebase UID
     * For unauthenticated users: uses IP address
     */
    private String getRateLimitKey(HttpServletRequest request, RateLimit rateLimit) {
        // Use custom key if provided
        if (!rateLimit.key().isEmpty()) {
            return rateLimit.key();
        }

        // Try to get authenticated user ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof FirebaseToken) {
            FirebaseToken token = (FirebaseToken) authentication.getPrincipal();
            return "user:" + token.getUid();
        }

        // Fall back to IP address for unauthenticated requests
        String clientIp = getClientIp(request);
        return "ip:" + clientIp;
    }

    /**
     * Extracts client IP address from request, considering proxy headers
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // If multiple IPs, take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
