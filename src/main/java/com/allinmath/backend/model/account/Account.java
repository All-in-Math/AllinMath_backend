package com.allinmath.backend.model.account;

import com.google.cloud.Timestamp;
import java.util.List;

public class Account {
    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
    private String bio;
    private String profileImageUrl;
    private List<String> sections;
    private Timestamp updatedAt;
    private boolean isEnabled;
    private AuthMeta authMeta;

    public Account() {}

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public List<String> getSections() { return sections; }
    public void setSections(List<String> sections) { this.sections = sections; }


    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    public AuthMeta getAuthMeta() { return authMeta; }
    public void setAuthMeta(AuthMeta authMeta) { this.authMeta = authMeta; }
}