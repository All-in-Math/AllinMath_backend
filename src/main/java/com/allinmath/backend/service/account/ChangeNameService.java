package com.allinmath.backend.service.account;

import com.allinmath.backend.dto.account.ChangeNameDTO;
import com.allinmath.backend.model.account.Account;
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
public class ChangeNameService {

    private final AccountRepository accountRepository;

    public ChangeNameService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void execute(ChangeNameDTO dto) throws Exception {
        // Start timer
        long startTime = System.currentTimeMillis();

        Logger.i("Changing name for user: %s", dto.userId());
        try {
            String fullName = dto.getFirstName() + " " + dto.getLastName();

            try {// Update Firebase Auth
                UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(dto.userId())
                        .setDisplayName(fullName);
                FirebaseAuth.getInstance().updateUser(request);
                Logger.d("Updated Firebase Auth display name for %s", dto.userId());
            } catch (FirebaseAuthException e) {
                throw new Exception("Failed to update Firebase Auth display name: " + e.getMessage());
            }

            // Update Firestore
            Account account = accountRepository.getAccount(dto.userId());
            if (account != null) {
                account.setFirstName(dto.getFirstName());
                account.setLastName(dto.getLastName());
                account.setUpdatedAt(Date.from(Instant.now()));
                accountRepository.updateAccount(account);

                // Stop timer and log duration
                long duration = System.currentTimeMillis() - startTime;
                Logger.i("Name change completed for %s in %d ms", dto.userId(), duration);
            } else {
                throw new Exception("User account not found.");
            }
        } catch (FirebaseAuthException e) {
            throw new Exception("Firebase Auth error during name change: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("System error while updating name.");
        }
    }
}
