package com.allinmath.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for testing the AllinMath backend API
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "AllinMath Backend API is running successfully!");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "allinmath-backend");
        response.put("version", "0.0.1-SNAPSHOT");
        return response;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "AllinMath Backend");
        response.put("description", "Backend API for AllinMath YKS preparation platform");
        response.put("developers", new String[]{"AllinMath Team"});
        response.put("environment", "development");
        return response;
    }
}