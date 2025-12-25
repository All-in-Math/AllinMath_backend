package com.allinmath.backend.service.assessment;

import com.allinmath.backend.dto.assessment.CreateAssessmentDTO;
import com.allinmath.backend.model.assessment.Assessment;
import com.allinmath.backend.model.assessment.AssessmentType;
import com.allinmath.backend.repository.AssessmentRepository;
import com.allinmath.backend.util.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.Date;
import com.allinmath.backend.model.assessment.AssessmentStatus;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;

    public AssessmentService(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    public String createAssessment(CreateAssessmentDTO dto, String createdBy) {
        Assessment assessment = new Assessment();
        assessment.setTitle(dto.getTitle());
        try {
            assessment.setType(AssessmentType.valueOf(dto.getType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid assessment type");
        }
        assessment.setDescription(dto.getDescription());
        assessment.setCourses(dto.getCourses());
        assessment.setQuestions(dto.getQuestions());
        assessment.setCreatedBy(createdBy);
        if (dto.getDeadlineAt() != null) {
            assessment.setDeadlineAt(new java.util.Date(dto.getDeadlineAt()));
        }

        // Initialize default values for required fields
        assessment.setSections(dto.getSections() != null ? dto.getSections() : java.util.Collections.emptyList());
        assessment.setTopics(dto.getTopics() != null ? dto.getTopics() : java.util.Collections.emptyList());
        assessment.setResources(dto.getResources() != null ? dto.getResources() : java.util.Collections.emptyList());
        assessment.setMaxScore(dto.getMaxScore() != null ? dto.getMaxScore() : 0.0);

        Date now = new Date();
        assessment.setCreatedAt(now);
        assessment.setUpdatedAt(now);
        assessment.setStatus(AssessmentStatus.DRAFT);

        try {
            return assessmentRepository.createAssessment(assessment);
        } catch (ExecutionException | InterruptedException e) {
            Logger.e("Error creating assessment: " + e.getMessage());
            throw new RuntimeException("Failed to create assessment");
        }
    }

    public void updateAssessment(com.allinmath.backend.dto.assessment.UpdateAssessmentDTO dto) {
        try {
            Assessment assessment = assessmentRepository.getAssessment(dto.getId());
            if (assessment == null) {
                throw new RuntimeException("Assessment not found");
            }

            if (dto.getTitle() != null)
                assessment.setTitle(dto.getTitle());
            if (dto.getDescription() != null)
                assessment.setDescription(dto.getDescription());
            if (dto.getCourses() != null)
                assessment.setCourses(dto.getCourses());
            if (dto.getSections() != null)
                assessment.setSections(dto.getSections());
            if (dto.getTopics() != null)
                assessment.setTopics(dto.getTopics());
            if (dto.getResources() != null)
                assessment.setResources(dto.getResources());
            if (dto.getQuestions() != null)
                assessment.setQuestions(dto.getQuestions());
            if (dto.getDeadlineAt() != null)
                assessment.setDeadlineAt(new java.util.Date(dto.getDeadlineAt()));
            if (dto.getMaxScore() != null)
                assessment.setMaxScore(dto.getMaxScore());
            if (dto.getStatus() != null)
                assessment.setStatus(dto.getStatus());

            assessment.setUpdatedAt(new Date());

            assessmentRepository.updateAssessment(assessment);
        } catch (ExecutionException | InterruptedException e) {
            Logger.e("Error updating assessment: " + e.getMessage());
            throw new RuntimeException("Failed to update assessment");
        }
    }

    public List<Assessment> getAssessmentsByCourse(String courseId, String type) {
        try {
            List<Assessment> assessments = assessmentRepository.getAssessmentsByCourse(courseId);
            if (type != null && !type.isEmpty()) {
                return assessments.stream()
                        .filter(a -> a.getType().name().equalsIgnoreCase(type))
                        .collect(Collectors.toList());
            }
            return assessments;
        } catch (ExecutionException | InterruptedException e) {
            Logger.e("Error getting assessments: " + e.getMessage());
            throw new RuntimeException("Failed to get assessments");
        }
    }

    public List<Assessment> getAssessmentsByCreator(String creatorId, String type) {
        try {
            List<Assessment> assessments = assessmentRepository.getAssessmentsByCreator(creatorId);
            if (type != null && !type.isEmpty()) {
                return assessments.stream()
                        .filter(a -> a.getType().name().equalsIgnoreCase(type))
                        .collect(Collectors.toList());
            }
            return assessments;
        } catch (ExecutionException | InterruptedException e) {
            Logger.e("Error getting assessments: " + e.getMessage());
            throw new RuntimeException("Failed to get assessments");
        }
    }

    public void deleteAssessment(String assessmentId, String userId) {
        try {
            Assessment assessment = assessmentRepository.getAssessment(assessmentId);
            if (assessment == null) {
                throw new RuntimeException("Assessment not found");
            }

            // Verify that the user is the creator of the assessment
            if (!assessment.getCreatedBy().equals(userId)) {
                throw new RuntimeException("Unauthorized to delete this assessment");
            }

            assessmentRepository.deleteAssessment(assessmentId);
        } catch (ExecutionException | InterruptedException e) {
            Logger.e("Error deleting assessment: " + e.getMessage());
            throw new RuntimeException("Failed to delete assessment");
        }
    }
}
