package com.allinmath.backend.dto.courses;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class UpdateCoursesDTO {
    @NotNull
    private List<String> tytCourses;

    @NotNull
    private List<String> aytCourses;

    public UpdateCoursesDTO() {
    }

    public UpdateCoursesDTO(List<String> tytCourses, List<String> aytCourses) {
        this.tytCourses = tytCourses;
        this.aytCourses = aytCourses;
    }

    public List<String> getTytCourses() {
        return tytCourses;
    }

    public void setTytCourses(List<String> tytCourses) {
        this.tytCourses = tytCourses;
    }

    public List<String> getAytCourses() {
        return aytCourses;
    }

    public void setAytCourses(List<String> aytCourses) {
        this.aytCourses = aytCourses;
    }
}
