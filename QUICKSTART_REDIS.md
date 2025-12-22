# Quick Start - Redis Integration & Rate Limiting

## For Developers

### Running the Application

1. **Without Redis** (Development):
```bash
# Disable rate limiting in .env or environment
RATE_LIMIT_ENABLED=false
```

2. **With Redis** (Production):
```bash
# Add to .env file
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your-password-here
RATE_LIMIT_ENABLED=true
```

### Adding Rate Limiting to New Endpoints

```java
@PostMapping("/your-endpoint")
@RateLimit(type = RateLimitType.SENSITIVE)  // or RateLimitType.DEFAULT
public ResponseEntity<?> yourMethod() {
    // Your code
}
```

### Rate Limit Types

| Type | Capacity | Refill Rate | Use Case |
|------|----------|-------------|----------|
| SENSITIVE | 10 req | 10/minute | Registration, password reset, email changes |
| DEFAULT | 100 req | 100/minute | Regular authenticated operations |

### Testing Rate Limiting

Run the included tests:
```bash
./mvnw test -Dtest=RateLimitServiceTest
./mvnw test -Dtest=RateLimitIntegrationTest
```

### Current Rate-Limited Endpoints

**Sensitive (10/min):**
- `POST /account/register`
- `POST /account/password/reset`
- `PUT /account/email/verify/sendEmail`
- `PUT /account/email/change`

**Default (100/min):**
- `POST /account/onboarding/complete`

### Error Response

When rate limit exceeded:
```json
HTTP 429 Too Many Requests

{
  "message": "Rate limit exceeded. Please try again later. Available tokens: 0"
}
```

## Configuration Reference

See `REDIS.md` for complete configuration details and architecture information.
