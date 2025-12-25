package com.allinmath.backend.model.account;

import java.util.List;

public class StudentProfile extends Account {
    private int gradeLevel;
    private List<String> teacherIDs;

    public StudentProfile(int gradeLevel, List<String> teacherIDs) {
        this.gradeLevel = gradeLevel;
        this.teacherIDs = teacherIDs;
    }

    public StudentProfile() {}

    public int getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(int gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public List<String> getTeacherIDs() {
        return teacherIDs;
    }

    public void setTeacherIDs(List<String> teacherIDs) {
        this.teacherIDs = teacherIDs;
    }

    public void addTeacherID(String teacherID) {
        if (!this.teacherIDs.contains(teacherID)) {
            this.teacherIDs.add(teacherID);
        }
    }

    public void removeTeacherID(String teacherID) {
        this.teacherIDs.remove(teacherID);
    }
}
