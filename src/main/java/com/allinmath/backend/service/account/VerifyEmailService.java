package com.allinmath.backend.service.account;

import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.repository.account.AccountRepository;
import com.allinmath.backend.util.Logger;
import com.google.cloud.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VerifyEmailService {

    private final AccountRepository accountRepository;

    // 1. Inject the repository via constructor
    public VerifyEmailService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void verify(String uid) {
        Logger.i("Verifying email for user: %s", uid);
        try {
            UserRecord user = FirebaseAuth.getInstance().getUser(uid);
            if (user.isEmailVerified()) {
                return;
            }

            // 2. Use the instance 'accountRepository', not the class name
            Account userDoc = accountRepository.getAccount(uid);

            if (userDoc != null && userDoc.getAuthMeta() != null) {
                Timestamp lastSent = userDoc.getAuthMeta().getLastEmailVerificationSentAt();

                // Rate limit: 60 seconds
                if (lastSent != null) {
                    long diff = Timestamp.now().getSeconds() - lastSent.getSeconds();
                    if (diff < 60) {
                        Logger.i("Skipping email verification, too soon: %s", uid);
                        return;
                    }
                }
            }

            String link = FirebaseAuth.getInstance().generateEmailVerificationLink(user.getEmail());
            Logger.i("Generated verification link for %s: %s", user.getEmail(), link);

            // TODO: Integrate email provider here (e.g., SendGrid, AWS SES)

        } catch (Exception e) {
            Logger.e("Failed to process email verification: %s", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to verify email. Please try again later.");
        }
    }
}