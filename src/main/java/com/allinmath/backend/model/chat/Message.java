package com.allinmath.backend.model.chat;

import java.util.List;
import java.util.Date;

public class Message {
    private String id;
    private String chatId;
    private String senderId;
    private String content;
    private List<String> attachments;
    private Date sentAt;
    private List<String> seenByIds;

    public Message() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public List<String> getSeenByIds() {
        return seenByIds;
    }

    public void setSeenByIds(List<String> seenByIds) {
        this.seenByIds = seenByIds;
    }
}
