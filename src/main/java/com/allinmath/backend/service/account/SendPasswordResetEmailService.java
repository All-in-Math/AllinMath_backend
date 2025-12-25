package com.allinmath.backend.service.account;

import com.allinmath.backend.dto.account.SendPasswordResetEmailDTO;
import com.allinmath.backend.util.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SendPasswordResetEmailService {

    private final Resend resend;

    @Value("${resend.from-email}")
    private String fromEmail;

    public SendPasswordResetEmailService(Resend resend) {
        this.resend = resend;
    }

    public void send(SendPasswordResetEmailDTO dto) {
        Logger.i("Requesting password reset for: %s", dto.getEmail());
        try {
            String link = FirebaseAuth.getInstance().generatePasswordResetLink(dto.getEmail());
            
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(dto.getEmail())
                    .subject("Reset your password for AllinMath")
                    .html("<p>You requested a password reset. Please click on the following link to reset your password: <a href=\"" + link + "\">" + link + "</a></p>")
                    .build();

            resend.emails().send(params);
            Logger.i("Password reset email sent to %s", dto.getEmail());
            
            // For dev/debug, we log it (Be careful with this in real prod logs!)
            Logger.d("Generated reset link for %s: %s", dto.getEmail(), link);
        } catch (Exception e) {
            // We log the error but do NOT throw it to the user to prevent email enumeration attacks.
            // (i.e., we don't want to tell a hacker "This email doesn't exist")
            Logger.e("Error generating reset link: %s", e.getMessage());
        }
    }
}