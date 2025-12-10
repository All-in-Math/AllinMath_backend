package com.allinmath.backend.dto.account;

import com.allinmath.backend.security.FirebaseAuthenticationToken;
import com.google.firebase.auth.FirebaseToken;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.context.SecurityContextHolder;

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

    public String userId() {
        // Extract user ID from security context
        FirebaseAuthenticationToken authToken =
                (FirebaseAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        FirebaseToken token = (FirebaseToken) authToken.getPrincipal();
        return token.getUid();
    }
}
