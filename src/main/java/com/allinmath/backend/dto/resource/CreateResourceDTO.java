package com.allinmath.backend.dto.resource;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class CreateResourceDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String url;

    private List<String> attachedTo;

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
