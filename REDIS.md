# Redis Integration and Rate Limiting

This document describes the Redis integration and rate limiting features added to AllinMath Backend.

## Overview

The application now includes Redis server integration with built-in rate limiting capabilities to:
- Prevent abuse of sensitive endpoints (registration, password reset, email verification)
- Improve performance through caching infrastructure
- Enable scalable distributed rate limiting

## Configuration

### Redis Connection

Add the following environment variables to your `.env` file or environment:

```properties
# Redis Configuration (optional - defaults shown)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# Rate Limiting Configuration (optional - defaults shown)
RATE_LIMIT_ENABLED=true
```

### Rate Limit Settings

The application.properties file contains the following default rate limit configurations:

**Default Rate Limit** (for regular endpoints):
- Capacity: 100 requests
- Refill: 100 tokens per 1 minute

**Sensitive Rate Limit** (for security-critical endpoints):
- Capacity: 10 requests
- Refill: 10 tokens per 1 minute

## Usage

### Applying Rate Limiting to Endpoints

Use the `@RateLimit` annotation on controller methods to apply rate limiting:

```java
@PostMapping("/account/register")
@RateLimit(type = RateLimitType.SENSITIVE)
public ResponseEntity<Map<String, String>> register(@Valid @RequestBody SignUpDTO dto) {
    // Your code here
}
```

### Rate Limit Types

- `RateLimitType.SENSITIVE`: Lower limits for security-critical endpoints (10 requests/minute)
- `RateLimitType.DEFAULT`: Standard limits for regular endpoints (100 requests/minute)

### Rate Limited Endpoints

The following endpoints are currently rate-limited:

**Sensitive endpoints (10 requests/minute per IP/user):**
- POST `/account/register` - User registration
- POST `/account/password/reset` - Password reset email
- PUT `/account/email/verify/sendEmail` - Email verification
- PUT `/account/email/change` - Email change

**Default endpoints (100 requests/minute per IP/user):**
- POST `/account/onboarding/complete` - Complete onboarding

## Error Handling

When a rate limit is exceeded, the API returns:

**HTTP Status:** 429 Too Many Requests

**Response Body:**
```json
{
  "message": "Rate limit exceeded. Please try again later. Available tokens: 0"
}
```

## Development

### Running without Redis

To disable rate limiting during development, set:

```properties
RATE_LIMIT_ENABLED=false
```

### Testing

Unit tests and integration tests are included in:
- `src/test/java/com/allinmath/backend/ratelimit/RateLimitServiceTest.java`
- `src/test/java/com/allinmath/backend/endpoint/RateLimitIntegrationTest.java`

## Architecture

The rate limiting implementation uses:

- **Bucket4j**: Token bucket algorithm for rate limiting
- **Redis**: Distributed storage (with local cache fallback)
- **Spring Interceptor**: Applied automatically to annotated endpoints

Rate limits are applied based on:
- **Authenticated requests**: User ID (Firebase UID)
- **Unauthenticated requests**: IP address (supports X-Forwarded-For headers)
