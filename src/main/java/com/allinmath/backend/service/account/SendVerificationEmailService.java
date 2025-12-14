package com.allinmath.backend.service.account;

import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.repository.account.AccountRepository;
import com.allinmath.backend.util.Logger;
import com.google.cloud.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SendVerificationEmailService {

    private final AccountRepository accountRepository;
    private final Resend resend;

    @Value("${resend.from-email}")
    private String fromEmail;

    // 1. Inject the repository via constructor
    public SendVerificationEmailService(AccountRepository accountRepository, Resend resend) {
        this.accountRepository = accountRepository;
        this.resend = resend;
    }

    public void verify(String uid) {
        Logger.i("Verifying email for user: %s", uid);
        try {
            UserRecord user = FirebaseAuth.getInstance().getUser(uid);
            if (user.isEmailVerified()) {
                return;
            }

            // Get the user doc from Firestore
            Account userDoc = accountRepository.getAccount(uid);

            if (userDoc != null && userDoc.getAuthMeta() != null) {
                Timestamp lastSent = userDoc.getAuthMeta().getLastEmailVerificationSentAt();

                // Rate limit: 60 seconds
                if (lastSent != null) {
                    long diff = Timestamp.now().getSeconds() - lastSent.getSeconds();
                    if (diff < 60) {
                        Logger.i("Email verification already sent for %s. Skipping.", user.getEmail());
                        return;
                    }
                }
            }

            String link = FirebaseAuth.getInstance().generateEmailVerificationLink(user.getEmail());
            Logger.i("Generated verification link for %s: %s", user.getEmail(), link);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(user.getEmail())
                    .subject("Verify your email for AllinMath")
                    .html("<p>Please verify your email by clicking on the following link: <a href=\"" + link + "\">" + link + "</a></p>")
                    .build();

            resend.emails().send(params);
            Logger.i("Verification email sent to %s", user.getEmail());

            Account account = accountRepository.getAccount(uid);
            account.getAuthMeta().setLastEmailVerificationSentAt(Timestamp.now());
            accountRepository.updateAccount(account);

        } catch (Exception e) {
            Logger.e("Failed to process email verification: %s", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to verify email. Please try again later.");
        }
    }
}