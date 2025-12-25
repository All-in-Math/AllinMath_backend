package com.allinmath.backend.controller;

import com.allinmath.backend.model.lesson.Lesson;
import com.allinmath.backend.service.LessonService;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lessons")
public class LessonController {
    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    /**
     * Get today's lessons for the authenticated teacher
     * POST /api/core/lessons/today
     */
    @PostMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodaysLessons(@RequestBody Map<String, String> request) {
        try {
            String teacherId = request.get("teacherId");

            if (teacherId == null || teacherId.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "teacherId is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            List<Lesson> lessons = lessonService.getTodaysLessons(teacherId);

            Map<String, Object> response = new HashMap<>();
            response.put("lessons", lessons);
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch lessons: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get all lessons for the authenticated teacher with optional status filter
     * POST /api/core/lessons/list-all
     */
    @PostMapping("/list-all")
    public ResponseEntity<Map<String, Object>> getAllLessons(@RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String teacherId = null;
            if (authentication != null && authentication.getPrincipal() instanceof FirebaseToken) {
                teacherId = ((FirebaseToken) authentication.getPrincipal()).getUid();
            }

            if (teacherId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            String status = request.get("status");
            List<Lesson> lessons = lessonService.getLessonsByTeacher(teacherId);

            if (status != null && !status.isEmpty()) {
                lessons = lessons.stream()
                        .filter(l -> l.getStatus() != null && l.getStatus().name().equalsIgnoreCase(status))
                        .collect(Collectors.toList());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("lessons", lessons);
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch lessons: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get all lessons for a teacher
     * POST /api/core/lessons/list
     */
    @PostMapping("/list")
    public ResponseEntity<Map<String, Object>> getLessonsByTeacher(@RequestBody Map<String, String> request) {
        try {
            String teacherId = request.get("teacherId");

            if (teacherId == null || teacherId.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "teacherId is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            List<Lesson> lessons = lessonService.getLessonsByTeacher(teacherId);

            Map<String, Object> response = new HashMap<>();
            response.put("lessons", lessons);
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch lessons: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get a lesson by ID
     * GET /api/core/lessons/{lessonId}
     */
    @GetMapping("/{lessonId}")
    public ResponseEntity<Map<String, Object>> getLessonById(@PathVariable String lessonId) {
        try {
            Lesson lesson = lessonService.getLessonById(lessonId);

            if (lesson == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Lesson not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("lesson", lesson);
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch lesson: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Create a new lesson
     * POST /api/core/lessons/create
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createLesson(@RequestBody Lesson lesson) {
        try {
            String lessonId = lessonService.createLesson(lesson);

            Map<String, Object> response = new HashMap<>();
            response.put("id", lessonId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ExecutionException | InterruptedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create lesson: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Update a lesson
     * POST /api/core/lessons/update
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateLesson(@RequestBody Map<String, Object> request) {
        try {
            String lessonId = (String) request.get("id");

            if (lessonId == null || lessonId.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Lesson ID is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Remove id from updates
            Map<String, Object> updates = new HashMap<>(request);
            updates.remove("id");

            lessonService.updateLesson(lessonId, updates);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", lessonId);
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update lesson: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Delete a lesson
     * DELETE /api/core/lessons/delete/{lessonId}
     */
    @DeleteMapping("/delete/{lessonId}")
    public ResponseEntity<Map<String, Object>> deleteLesson(@PathVariable String lessonId) {
        try {
            lessonService.deleteLesson(lessonId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", lessonId);
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete lesson: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
