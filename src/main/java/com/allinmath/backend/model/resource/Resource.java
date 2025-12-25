package com.allinmath.backend.model.resource;

import java.util.List;

public class Resource {
    private String id;
    private String title;
    private String url;
    private String uploadedBy;
    private String uploadedAt; // Using String for Timestamp for simplicity in POJO, or use Date/Timestamp
    private List<String> attachedTo;

    public Resource() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(String uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public List<String> getAttachedTo() {
        return attachedTo;
    }

    public void setAttachedTo(List<String> attachedTo) {
        this.attachedTo = attachedTo;
    }
}
