package com.allinmath.backend.service.account;

import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.repository.account.AccountRepository;
import com.allinmath.backend.util.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ChangeEmailService {

    private final AccountRepository accountRepository;
    private final Resend resend;

    @Value("${resend.from-email}")
    private String fromEmail;

    public ChangeEmailService(AccountRepository accountRepository, Resend resend) {
        this.accountRepository = accountRepository;
        this.resend = resend;
    }

    public void change(String uid, String currentEmail, String newEmail) {
        Logger.i("Changing email for user: %s", uid);

        try {
            // Check if old and new emails are the same
            if (currentEmail.equalsIgnoreCase(newEmail)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New email cannot be the same as the old email.");
            }

            // Verify old email matches current auth email
            UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
            if (!userRecord.getEmail().equalsIgnoreCase(currentEmail)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong. Please try again.");
            }

            // Update Firebase Auth
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                    .setEmail(newEmail);
            FirebaseAuth.getInstance().updateUser(request);
            Logger.i("Updated Firebase Auth email for user: %s", uid);

            try {
                // Update Firestore
                Account account = accountRepository.getAccount(uid);
                if (account != null) {
                    account.setEmail(newEmail);
                    accountRepository.updateAccount(account);
                    Logger.i("Updated Firestore account email for user: %s", uid);

                    // Send welcome email
                    CreateEmailOptions welcomeParams = CreateEmailOptions.builder()
                            .from(fromEmail)
                            .to(newEmail)
                            .subject("Welcome to AllinMath!")
                            .html("<p>Dear " + account.getFirstName() + ",</p><p>Your email has been updated. Welcome to AllinMath! We're excited to have you on board.</p>")
                            .build();
                    resend.emails().send(welcomeParams);
                    Logger.i("Sent welcome email to: %s", newEmail);

                    // Send verification email by calling the existing service
                    SendVerificationEmailService sendVerificationEmailService = new SendVerificationEmailService(accountRepository, resend);
                    sendVerificationEmailService.send(userRecord.getUid());
                    Logger.i("Sent verification email to: %s", newEmail);
                    
                } else {
                    Logger.w("Account not found in Firestore for user: %s", uid);
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "An error occurred while fetching your account data.");
                }
            } catch (Exception e) {
                Logger.e("Failed to update Firestore or send email: %s", e.getMessage());
                // Rollback Auth change
                try {
                    UserRecord.UpdateRequest rollbackRequest = new UserRecord.UpdateRequest(uid)
                            .setEmail(currentEmail);
                    FirebaseAuth.getInstance().updateUser(rollbackRequest);
                    Logger.w("Rolled back Auth email change for user: %s", uid);
                } catch (Exception rollbackEx) {
                    Logger.e("Failed to rollback Auth email change: %s", rollbackEx.getMessage());
                }

                if (e instanceof ResponseStatusException) {
                    throw (ResponseStatusException) e;
                }
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update email. Please try again.");
            }

        } catch (FirebaseAuthException e) {
            Logger.e("Firebase Auth Error: %s", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or email already in use.");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            Logger.e("Unexpected error during email change: %s", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }
}
