package com.allinmath.backend.dto.assessment;

import com.allinmath.backend.model.assessment.AssessmentStatus;
import com.allinmath.backend.model.assessment.Question;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class CreateAssessmentDTO {
    @NotBlank
    private String title;

    private String type; // "homework" | "quiz" | "exam"
    private String description;
    private List<String> courses;
    private List<String> sections;
    private List<String> topics;
    private List<String> resources;
    private List<Question> questions;
    private Long deadlineAt;
    private Double maxScore;
    private AssessmentStatus status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Long getDeadlineAt() {
        return deadlineAt;
    }

    public void setDeadlineAt(Long deadlineAt) {
        this.deadlineAt = deadlineAt;
    }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    public AssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssessmentStatus status) {
        this.status = status;
    }
}
