package com.allinmath.backend.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ChangeEmailDTO {

    @NotBlank(message = "Old email is required")
    @Email(message = "Invalid old email format")
    private String currentEmail;

    @NotBlank(message = "New email is required")
    @Email(message = "Invalid new email format")
    private String newEmail;

    public String getCurrentEmail() {
        return currentEmail;
    }

    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
