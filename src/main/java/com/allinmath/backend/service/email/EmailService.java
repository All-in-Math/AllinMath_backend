package com.allinmath.backend.service.email;

import com.allinmath.backend.util.Logger;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    /**
     * Sends a password reset email with the provided reset link.
     *
     * @param toEmail The recipient's email address
     * @param resetLink The password reset link to include in the email
     * @throws Exception if the email fails to send
     */
    public void sendPasswordResetEmail(String toEmail, String resetLink) throws Exception {
        Logger.i("Sending password reset email to: %s", toEmail);
        
        try {
            CreateEmailOptions emailOptions = CreateEmailOptions.builder()
                    .from("AllinMath <noreply@allinmath.com>")
                    .to(toEmail)
                    .subject("Password Reset Request")
                    .html(buildPasswordResetHtml(resetLink))
                    .build();

            CreateEmailResponse response = resend.emails().send(emailOptions);
            Logger.d("Password reset email sent successfully. ID: %s", response.getId());
        } catch (ResendException e) {
            Logger.e("Failed to send password reset email: %s", e.getMessage());
            throw new Exception("Failed to send password reset email: " + e.getMessage());
        }
    }

    /**
     * Sends a welcome email to a newly registered user.
     *
     * @param toEmail The recipient's email address
     * @param firstName The user's first name
     * @throws Exception if the email fails to send
     */
    public void sendWelcomeEmail(String toEmail, String firstName) throws Exception {
        Logger.i("Sending welcome email to: %s", toEmail);
        
        try {
            CreateEmailOptions emailOptions = CreateEmailOptions.builder()
                    .from("AllinMath <welcome@allinmath.com>")
                    .to(toEmail)
                    .subject("Welcome to AllinMath!")
                    .html(buildWelcomeHtml(firstName))
                    .build();

            CreateEmailResponse response = resend.emails().send(emailOptions);
            Logger.d("Welcome email sent successfully. ID: %s", response.getId());
        } catch (ResendException e) {
            Logger.e("Failed to send welcome email: %s", e.getMessage());
            throw new Exception("Failed to send welcome email: " + e.getMessage());
        }
    }

    private String buildPasswordResetHtml(String resetLink) {
        return String.format(
                "<html>" +
                "<head><style>body{font-family:Arial,sans-serif;}</style></head>" +
                "<body>" +
                "<h2>Password Reset Request</h2>" +
                "<p>You have requested to reset your password. Click the link below to proceed:</p>" +
                "<p><a href=\"%s\" style=\"background-color:#4CAF50;color:white;padding:10px 20px;text-decoration:none;border-radius:5px;\">Reset Password</a></p>" +
                "<p>If you did not request this, please ignore this email.</p>" +
                "<p>This link will expire in 1 hour.</p>" +
                "<p>Best regards,<br>The AllinMath Team</p>" +
                "</body>" +
                "</html>",
                resetLink
        );
    }

    private String buildWelcomeHtml(String firstName) {
        return String.format(
                "<html>" +
                "<head><style>body{font-family:Arial,sans-serif;}</style></head>" +
                "<body>" +
                "<h2>Welcome to AllinMath, %s!</h2>" +
                "<p>Thank you for joining AllinMath. We're excited to have you on board!</p>" +
                "<p>Get started by logging in to your account and exploring our features.</p>" +
                "<p>If you have any questions, feel free to reach out to our support team.</p>" +
                "<p>Best regards,<br>The AllinMath Team</p>" +
                "</body>" +
                "</html>",
                firstName
        );
    }
}
