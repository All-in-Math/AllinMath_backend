package com.allinmath.backend.service.account;

import com.allinmath.backend.dto.account.ChangeNameDTO;
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
public class ChangeNameService {

    private final AccountRepository accountRepository;

    public ChangeNameService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void execute(String uid, ChangeNameDTO dto) throws Exception {
        Logger.i("Changing name for user: %s", uid);
        try {
            String fullName = dto.getFirstName() + " " + dto.getLastName();

            // Update Firebase Auth
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                    .setDisplayName(fullName);
            FirebaseAuth.getInstance().updateUser(request);
            Logger.d("Updated Firebase Auth display name for %s", uid);

            // Update Firestore
            Account account = accountRepository.getAccount(uid);
            if (account != null) {
                account.setFirstName(dto.getFirstName());
                account.setLastName(dto.getLastName());
                account.setUpdatedAt(Date.from(Instant.now()));
                accountRepository.updateAccount(account);
                Logger.d("Updated Firestore account name for %s", uid);
            } else {
                Logger.w("User account not found in Firestore for %s", uid);
                throw new Exception("User account not found.");
            }
        } catch (FirebaseAuthException e) {
             Logger.e("Firebase Auth error during name change: %s", e.getMessage());
             throw new Exception("Failed to update name: " + e.getMessage());
        } catch (Exception e) {
             Logger.e("Unexpected error during name change: %s", e.getMessage());
             throw new Exception("System error while updating name.");
        }
    }
}
