package com.allinmath.backend.service.account;

import com.allinmath.backend.dto.account.SendPasswordResetEmailDTO;
import com.allinmath.backend.util.Logger;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.stereotype.Service;

@Service
public class SendPasswordResetEmailService {

    public void send(SendPasswordResetEmailDTO dto) {
        Logger.i("Requesting password reset for: %s", dto.getEmail());
        try {
            String link = FirebaseAuth.getInstance().generatePasswordResetLink(dto.getEmail());
            // TODO: In production, send this link via email provider (e.g. SendGrid/Resend)
            // For dev/debug, we log it (Be careful with this in real prod logs!)
            Logger.d("Generated reset link for %s: %s", dto.getEmail(), link);
        } catch (Exception e) {
            // We log the error but do NOT throw it to the user to prevent email enumeration attacks.
            // (i.e., we don't want to tell a hacker "This email doesn't exist")
            Logger.e("Error generating reset link: %s", e.getMessage());
        }
    }
}