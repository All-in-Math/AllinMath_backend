package com.allinmath.backend;

import com.allinmath.backend.util.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application for AllinMath Backend
 * <p>
 * This application provides REST API endpoints for the AllinMath platform,
 * serving students, teachers, and administrators.
 */
@SpringBootApplication
public class AllinmathBackendApplication {

    public static void main(String[] args) {
        Logger.i("Starting AllinMath Backend Application...");
        try {
            SpringApplication.run(AllinmathBackendApplication.class, args);
        } catch (Exception e) {
            Logger.f("Failed to start AllinMath Backend Application", e);
        }
    }

}
