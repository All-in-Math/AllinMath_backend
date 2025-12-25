package com.allinmath.backend.dto.student;

import jakarta.validation.constraints.NotBlank;

public class EnrollTeacherDTO {

    @NotBlank(message = "Student ID is required")
    private String studentId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
