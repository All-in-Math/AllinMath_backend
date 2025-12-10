package com.allinmath.backend.model.lesson;

import java.util.List;
import java.util.Date;

public class Lesson {
    private String id;
    private List<String> sections;
    private List<String> courses;
    private List<String> topics;
    private String title;
    private String description;
    private String teacherId;
    private String studentId;
    private List<String> resources;
    private Date scheduledStartAt;
    private Date scheduledEndAt;
    private LessonStatus status;
    private String meetingLink;
    private String location;
    private Date createdAt;
    private Date updatedAt;

    public Lesson() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public Date getScheduledStartAt() {
        return scheduledStartAt;
    }

    public void setScheduledStartAt(Date scheduledStartAt) {
        this.scheduledStartAt = scheduledStartAt;
    }

    public Date getScheduledEndAt() {
        return scheduledEndAt;
    }

    public void setScheduledEndAt(Date scheduledEndAt) {
        this.scheduledEndAt = scheduledEndAt;
    }

    public LessonStatus getStatus() {
        return status;
    }

    public void setStatus(LessonStatus status) {
        this.status = status;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
