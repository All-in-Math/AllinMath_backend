package com.allinmath.backend.service.account;

import com.allinmath.backend.dto.account.SendPasswordResetEmailDTO;
import com.allinmath.backend.service.email.EmailService;
import com.allinmath.backend.util.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendPasswordResetEmailService {

    @Autowired
    private EmailService emailService;

    public void execute(SendPasswordResetEmailDTO dto) throws Exception {
        Logger.i("Requesting password reset for: %s", dto.getEmail());
        try {
            String link = FirebaseAuth.getInstance().generatePasswordResetLink(dto.getEmail());
            Logger.d("Generated reset link for %s", dto.getEmail());
            
            // Send password reset email via Resend
            emailService.sendPasswordResetEmail(dto.getEmail(), link);
            
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
