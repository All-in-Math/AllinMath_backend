package com.allinmath.backend.service;

import com.allinmath.backend.model.lesson.Lesson;
import com.allinmath.backend.repository.LessonRepository;
import com.google.cloud.Timestamp;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class LessonService {
    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    /**
     * Get today's lessons for a teacher
     */
    public List<Lesson> getTodaysLessons(String teacherId) throws ExecutionException, InterruptedException {
        // Get start and end of today
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // Convert to Timestamp
        Timestamp startTimestamp = Timestamp.of(Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()));
        Timestamp endTimestamp = Timestamp.of(Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()));

        return lessonRepository.getLessonsByTeacherAndDate(teacherId, startTimestamp, endTimestamp);
    }

    /**
     * Get all lessons for a teacher
     */
    public List<Lesson> getLessonsByTeacher(String teacherId) throws ExecutionException, InterruptedException {
        return lessonRepository.getLessonsByTeacher(teacherId);
    }

    /**
     * Get a lesson by ID
     */
    public Lesson getLessonById(String lessonId) throws ExecutionException, InterruptedException {
        return lessonRepository.getLessonById(lessonId);
    }

    /**
     * Create a new lesson
     */
    public String createLesson(Lesson lesson) throws ExecutionException, InterruptedException {
        return lessonRepository.createLesson(lesson);
    }

    /**
     * Update a lesson
     */
    public void updateLesson(String lessonId, Map<String, Object> updates)
            throws ExecutionException, InterruptedException {
        lessonRepository.updateLesson(lessonId, updates);
    }

    /**
     * Delete a lesson
     */
    public void deleteLesson(String lessonId) throws ExecutionException, InterruptedException {
        lessonRepository.deleteLesson(lessonId);
    }
}
