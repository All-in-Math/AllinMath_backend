package com.allinmath.backend.model.account;

public class StudentProfile extends Account {
    private int gradeLevel;

    public StudentProfile(int gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public int getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(int gradeLevel) {
        this.gradeLevel = gradeLevel;
    }
}
