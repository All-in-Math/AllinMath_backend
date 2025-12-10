package com.allinmath.backend.model.assessment;

public class Choice {
    private String text;
    private String attachmentUrl;

    public Choice() {}

    public Choice(String text, String attachmentUrl) {
        this.text = text;
        this.attachmentUrl = attachmentUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }
}
