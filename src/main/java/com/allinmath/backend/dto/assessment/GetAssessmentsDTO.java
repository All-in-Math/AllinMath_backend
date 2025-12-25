package com.allinmath.backend.dto.assessment;

import jakarta.validation.constraints.NotBlank;

public class GetAssessmentsDTO {
    @NotBlank(message = "Course name is required")
    private String courseName;
    private String type;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
