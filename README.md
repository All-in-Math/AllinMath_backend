# AllinMath Backend

AllinMath backend service built with Spring Boot.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.9+
- Firebase service account credentials

### Environment Variables

Copy `.env.example` to `.env` and fill in the required values:

```bash
cp .env.example .env
```

Required environment variables:

- `FIREBASE_CONFIG_PATH`: Path to your Firebase service account JSON file
- `FIREBASE_STORAGE_BUCKET`: Your Firebase storage bucket
- `FIREBASE_PROJECT_ID`: Your Firebase project ID
- `RESEND_API_KEY`: Your Resend API key for sending emails
- `STATSIG_SECRET`: Your Statsig server secret key for feature flags

Optional (for CI/CD):
- `SENTRY_AUTH_TOKEN`: Sentry authentication token for uploading source bundles

### Building the Application

```bash
./mvnw clean package
```

### Running the Application

```bash
./mvnw spring-boot:run
```

The application will start on port 8080 with context path `/core/v1`.

## Integrations

### Resend Email Service

The application uses [Resend](https://resend.com) for sending transactional emails:
- Welcome emails when new users register
- Password reset emails

Configuration is in `application.properties` under `resend.api.key`.

### Sentry Error Tracking

[Sentry](https://sentry.io) is integrated for error tracking and monitoring:
- Automatic error capture and reporting
- Source code context in stack traces (when SENTRY_AUTH_TOKEN is set in CI/CD)
- Request data collection (PII enabled)

Configuration is in `application.properties` under `sentry.*`.

### Statsig Feature Flags

[Statsig](https://statsig.com) is integrated for feature flags and A/B testing:
- Server-side feature flag evaluation
- Experiment tracking
- Analytics

The `StatsigServer` bean is available for injection in your services.

Configuration is in `application.properties` under `statsig.server.secret`.

## API Documentation

The API is available at `http://localhost:8080/core/v1/`

## License

See LICENSE file for details.
