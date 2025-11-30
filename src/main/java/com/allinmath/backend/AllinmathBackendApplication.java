package com.allinmath.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application for AllinMath Backend
 * 
 * This application provides REST API endpoints for the AllinMath platform,
 * serving students, teachers, and administrators.
 */
@SpringBootApplication
public class AllinmathBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AllinmathBackendApplication.class, args);
	}

}
