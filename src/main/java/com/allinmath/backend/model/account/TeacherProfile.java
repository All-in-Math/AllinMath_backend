package com.allinmath.backend.model.account;

import java.util.List;

public class TeacherProfile extends Account {
    private String inviteCode;
    private double hourlyRate;
    private List<String> courses;

    public TeacherProfile() {}

    public TeacherProfile(String inviteCode, double hourlyRate, List<String> courses) {
        this.inviteCode = inviteCode;
        this.hourlyRate = hourlyRate;
        this.courses = courses;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }
}
