package com.allinmath.backend.dto.resource;

import jakarta.validation.constraints.NotBlank;

public class GetResourcesDTO {
    @NotBlank(message = "Course name is required")
    private String courseName;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
