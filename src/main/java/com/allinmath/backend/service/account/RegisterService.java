package com.allinmath.backend.service.account;

import com.allinmath.backend.dto.account.SignUpDTO;
import com.allinmath.backend.service.email.EmailService;
import com.allinmath.backend.util.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegisterService {
    
    @Autowired
    private EmailService emailService;
    
    public String register(SignUpDTO dto) throws Exception {
        // Start timer
        long startTime = System.currentTimeMillis();

        Logger.i("Attempting to register new user: %s", dto.getEmail());
        try {
            // 1. Create User in Firebase Auth
            UserRecord.CreateRequest newUserRequestDetails = new UserRecord.CreateRequest()
                    .setEmail(dto.getEmail())
                    .setPassword(dto.getPassword())
                    .setDisplayName(dto.getFirstName() + " " + dto.getLastName());

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(newUserRequestDetails);
            Logger.d("Firebase Auth user created: %s", userRecord.getUid());

            // 2. Send welcome email
            try {
                emailService.sendWelcomeEmail(dto.getEmail(), dto.getFirstName());
            } catch (Exception e) {
                // Log the error but don't fail registration
                Logger.w("Failed to send welcome email, but registration succeeded: %s", e.getMessage());
            }

            // 3. Generate Custom Token for immediate sign-in
            String customToken = FirebaseAuth.getInstance().createCustomToken(userRecord.getUid());

            // End timer
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            Logger.i("Registration successful for: %s. in %d ms", dto.getEmail(), duration);
            
            return customToken;
        } catch (FirebaseAuthException e) {
            Logger.e("Firebase Auth Error during registration: %s", e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("EMAIL_EXISTS")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The user with the provided email already exists.");
            }
            throw new Exception("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            Logger.e("Unexpected error during registration: %s", e.getMessage());
            throw new Exception("An unexpected error occurred during registration.");
        }
    }
}
