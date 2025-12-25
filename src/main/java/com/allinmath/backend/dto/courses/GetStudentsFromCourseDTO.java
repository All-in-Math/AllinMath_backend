package com.allinmath.backend.dto.courses;

public class GetStudentsFromCourseDTO {
    private String courseName;
    private String teacherID;

    public GetStudentsFromCourseDTO() {}

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }
}
