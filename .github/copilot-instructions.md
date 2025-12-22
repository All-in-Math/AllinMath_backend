# Copilot Instructions for AllinMath Backend

This document provides guidance for GitHub Copilot when working on the AllinMath Backend project.

## Project Overview

AllinMath Backend is a Spring Boot REST API application that serves students, teachers, and administrators. The application uses Firebase for authentication and Firestore as the database, with additional integrations for Sentry (error tracking), Statsig (feature flags), and Resend (email services).

## Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Java Version**: Java 17
- **Build Tool**: Maven
- **Database**: Firebase Firestore
- **Authentication**: Firebase Auth
- **Additional Services**:
  - Sentry for error tracking
  - Statsig for feature flags
  - Resend for email services
  - Spring Security for endpoint security

## Build and Test Commands

### Building the Project
```bash
# Build the project
./mvnw clean install

# Build without running tests
./mvnw clean install -DskipTests
```

### Running the Application
```bash
# Run the application (Unix/Linux/Mac)
./mvnw spring-boot:run

# Run the application (Windows)
mvnw.cmd spring-boot:run
```

### Testing
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ClassName

# Run with coverage
./mvnw test jacoco:report
```

## Project Structure

```
src/main/java/com/allinmath/backend/
├── config/              # Configuration classes (Firebase, Security, Web)
├── controller/          # General controllers (e.g., HealthController)
├── endpoint/            # REST API endpoints (main controllers)
├── dto/                 # Data Transfer Objects
│   └── account/         # Account-related DTOs
├── exception/           # Custom exception classes
├── model/               # Domain models
│   ├── account/         # User accounts and profiles
│   ├── assessment/      # Assessment-related models
│   ├── availability/    # Teacher availability models
│   ├── chat/            # Chat and messaging models
│   ├── complaint/       # Complaint models
│   ├── feedback/        # Feedback models
│   ├── lesson/          # Lesson-related models
│   └── notification/    # Notification models
├── repository/          # Firestore data access layer
│   └── account/         # Account repositories
├── security/            # Security configuration and filters
├── service/             # Business logic layer
│   ├── account/         # Account-related services
│   └── assesments/      # Assessment services
├── storage/             # Storage services (e.g., Firebase Storage)
└── util/                # Utility classes (e.g., Logger)

src/main/resources/
└── application.properties  # Application configuration
```

## Code Conventions

### General Guidelines

1. **Package Organization**: Follow the layered architecture pattern:
   - `endpoint` - REST controllers (use `@RestController` and `@RequestMapping`)
   - `service` - Business logic (use `@Service`)
   - `repository` - Data access layer (use `@Repository`)
   - `model` - Domain entities (POJOs)
   - `dto` - Data transfer objects with validation
   - `config` - Configuration classes (use `@Configuration`)

2. **Naming Conventions**:
   - Controllers: `*Controller.java` (in `endpoint` or `controller` package)
   - Services: `*Service.java`
   - Repositories: `*Repository.java`
   - DTOs: `*DTO.java`
   - Models: Use descriptive nouns (e.g., `Account.java`, `Lesson.java`)

3. **Dependency Injection**: Use constructor-based dependency injection (preferred over field injection)

4. **Validation**: Use Jakarta Bean Validation annotations on DTOs:
   - `@NotBlank`, `@Email`, `@Size`, etc.
   - Apply `@Valid` on controller method parameters

5. **Error Handling**: 
   - Create custom exceptions in the `exception` package
   - Use appropriate HTTP status codes in responses
   - Log errors using the custom `Logger` utility class

6. **Logging**: Use the custom `Logger` utility class located in `com.allinmath.backend.util.Logger`:
   - `Logger.i()` for info messages
   - `Logger.e()` for error messages  
   - `Logger.f()` for fatal errors
   - `Logger.d()` for debug messages

### Code Style

1. **Model Classes**:
   - Use POJOs with getters and setters
   - Include a no-args constructor
   - Example:
   ```java
   public class Account {
       private String uid;
       private String firstName;
       
       public Account() {}
       
       public String getUid() { return uid; }
       public void setUid(String uid) { this.uid = uid; }
   }
   ```

2. **DTOs**:
   - Include validation annotations
   - Only include getters (and setters where needed for binding)
   - Example:
   ```java
   public class SignUpDTO {
       @NotBlank(message = "First name is required")
       @Size(max = 64, message = "First name must be less than 64 characters")
       private String firstName;
       
       public String getFirstName() { return firstName; }
   }
   ```

3. **Controllers**:
   - Use `@RestController` annotation
   - Define base path with `@RequestMapping`
   - Return `ResponseEntity<Map<String, String>>` for simple responses
   - Use `@AuthenticationPrincipal FirebaseToken token` for authenticated endpoints
   - Example:
   ```java
   @RestController
   @RequestMapping("/account")
   public class AccountController {
       private final RegisterService registerService;
       
       public AccountController(RegisterService registerService) {
           this.registerService = registerService;
       }
       
       @PostMapping("/register")
       public ResponseEntity<Map<String, String>> register(@Valid @RequestBody SignUpDTO dto) {
           String token = registerService.register(dto);
           return ResponseEntity.ok(Collections.singletonMap("message", "Success"));
       }
   }
   ```

4. **Services**:
   - Use `@Service` annotation
   - Keep methods focused on single responsibility
   - Handle business logic and call repositories

5. **Repositories**:
   - Use `@Repository` annotation
   - Access Firestore via `FirestoreClient.getFirestore()`
   - Use `ApiFuture` for async operations
   - Handle `ExecutionException` and `InterruptedException`

### Firebase Integration

- **Authentication**: Use `FirebaseToken` for user identity in controllers
- **Firestore**: Collections are accessed via `getFirestore().collection("collectionName")`
- **Storage**: Firebase Storage is used for profile pictures and other files

### Environment Variables

The application uses environment variables for configuration. Required variables include:
- `FIREBASE_CONFIG_PATH` - Path to Firebase service account JSON
- `FIREBASE_PROJECT_ID` - Firebase project ID
- `FIREBASE_STORAGE_BUCKET` - Firebase Storage bucket name
- `SENTRY_DSN` - Sentry DSN for error tracking
- `STATSIG_SECRET` - Statsig secret key
- `RESEND_API_KEY` - Resend API key for emails
- `RESEND_FROM_EMAIL` - From email address

These can be set in a `.env` file at the project root (not committed to git).

### API Design

- **Base Path**: `/core/v1` (configured in `application.properties`)
- **Response Format**: Use `Map<String, String>` for simple responses
- **Status Codes**: Use appropriate HTTP status codes (200, 400, 401, 404, 500, etc.)
- **Security**: Suppress sensitive error details to users; log them internally

### Documentation

- Use Javadoc comments for classes and public methods
- Keep comments concise and meaningful
- Document complex business logic

## Common Tasks

### Adding a New Endpoint

1. Create DTO in `dto/` package with validation
2. Create/update service in `service/` package
3. Add endpoint method in appropriate controller in `endpoint/`
4. Test the endpoint

### Adding a New Model

1. Create POJO in appropriate `model/` subdirectory
2. Add getters/setters and no-args constructor
3. Create corresponding repository if needed
4. Create service methods for business logic

### Adding Firebase Collections

1. Create model class for the document structure
2. Create repository class with CRUD methods
3. Use `getFirestore().collection("collectionName")`
4. Handle futures with `.get()` and proper exception handling

## Testing

- Write unit tests for services
- Use `@SpringBootTest` for integration tests
- Mock Firebase dependencies when needed
- Test validation on DTOs

## Security Considerations

- Always validate and sanitize user inputs
- Use Firebase Auth tokens for authentication
- Don't expose sensitive error messages to clients
- Log security events appropriately
- Use environment variables for secrets (never hardcode)
