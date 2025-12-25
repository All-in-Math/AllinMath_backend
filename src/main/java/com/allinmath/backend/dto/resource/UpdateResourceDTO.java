package com.allinmath.backend.dto.resource;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class UpdateResourceDTO {
    @NotNull
    private String id;
    private String title;
    private String url;
    private List<String> attachedTo;

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

    public List<String> getAttachedTo() {
        return attachedTo;
    }

    public void setAttachedTo(List<String> attachedTo) {
        this.attachedTo = attachedTo;
    }
}
