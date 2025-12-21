package com.allinmath.backend.dto.account;

import com.allinmath.backend.model.account.UserRole;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CompleteOnboardingDTO {
    @NotNull
    private UserRole role;

    @NotNull
    @Min(13)
    @Max(100)
    private int age;

    @NotNull
    private List<String> sections;

    private List<String> tytCourses;

    private List<String> aytCourses;

    public CompleteOnboardingDTO() {}

    public CompleteOnboardingDTO(UserRole role, int age, List<String> sections, List<String> tytCourses, List<String> aytCourses) {
        this.role = role;
        this.age = age;
        this.sections = sections;
        this.tytCourses = tytCourses;
        this.aytCourses = aytCourses;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
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
