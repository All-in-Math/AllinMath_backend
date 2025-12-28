package com.allinmath.backend.model.account;

import java.util.List;

public class TeacherProfile extends Account {
    private String inviteCode;
    private double hourlyRate;
    private double rating;
    private java.util.List<String> tytCourses;
    private java.util.List<String> aytCourses;

    public TeacherProfile() {}

    public TeacherProfile(String inviteCode, double hourlyRate, double rating, List<String> tytCourses, List<String> aytCourses) {
        this.inviteCode = inviteCode;
        this.hourlyRate = hourlyRate;
        this.rating = rating;
        this.tytCourses = tytCourses;
        this.aytCourses = aytCourses;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
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
