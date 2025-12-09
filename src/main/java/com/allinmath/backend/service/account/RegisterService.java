package com.allinmath.backend.service.account;

import com.allinmath.backend.dto.account.SignUpDTO;
import com.allinmath.backend.model.Account;
import com.allinmath.backend.repository.account.AccountRepository;
import com.allinmath.backend.util.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Service
public class RegisterService {
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

            // 2. Generate Custom Token for immediate sign-in
            String customToken = FirebaseAuth.getInstance().createCustomToken(userRecord.getUid());

            // End timer
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            Logger.i("Registration successful for: %s. in %d ms", dto.getEmail(), duration);
            
            return customToken;
        } catch (FirebaseAuthException e) {
            Logger.e("Firebase Auth Error during registration: %s", e.getMessage());
            throw new Exception("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            Logger.e("Unexpected error during registration: %s", e.getMessage());
            throw new Exception("An unexpected error occurred during registration.");
        }
    }
}
