package com.allinmath.backend.model.availability;

import java.util.Date;
import java.util.List;

public class TeacherAvailability {
    private String id;
    private String teacherId;
    private WeeklyTemplate weeklyTemplate;
    private List<AvailabilityException> exceptions;
    private Date updatedAt;

    public TeacherAvailability() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public WeeklyTemplate getWeeklyTemplate() {
        return weeklyTemplate;
    }

    public void setWeeklyTemplate(WeeklyTemplate weeklyTemplate) {
        this.weeklyTemplate = weeklyTemplate;
    }

    public List<AvailabilityException> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<AvailabilityException> exceptions) {
        this.exceptions = exceptions;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
