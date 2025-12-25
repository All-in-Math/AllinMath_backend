package com.allinmath.backend.dto.account;

import jakarta.validation.constraints.NotBlank;

public class ChangeNameDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
