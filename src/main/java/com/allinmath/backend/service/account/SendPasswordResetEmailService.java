package com.allinmath.backend.service.account;

import com.allinmath.backend.dto.account.SendPasswordResetEmailDTO;
import com.allinmath.backend.util.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.stereotype.Service;

@Service
public class SendPasswordResetEmailService {

    public void execute(SendPasswordResetEmailDTO dto) throws Exception {
        Logger.i("Requesting password reset for: %s", dto.getEmail());
        try {
            String link = FirebaseAuth.getInstance().generatePasswordResetLink(dto.getEmail());
            // In production, send this link via email provider
            System.out.println("Password Reset Link for " + dto.getEmail() + ": " + link);
            Logger.d("Generated reset link for %s", dto.getEmail());
        } catch (FirebaseAuthException e) {
             // For privacy, we usually don't want to reveal if email exists or not, but standard firebase error might.
             // Here we wrap it.
             Logger.e("Firebase error generating reset link: %s", e.getMessage());
             throw new Exception("Could not generate reset link: " + e.getMessage());
        } catch (Exception e) {
            Logger.e("Unexpected error generating reset link: %s", e.getMessage());
            throw new Exception("System error while processing password reset.");
        }
    }
}
