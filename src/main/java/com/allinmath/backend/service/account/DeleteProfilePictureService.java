package com.allinmath.backend.service.account;

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
public class DeleteProfilePictureService {

    private final AccountRepository accountRepository;

    public DeleteProfilePictureService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void execute(String uid) throws Exception {
        Logger.i("Deleting profile picture for user: %s", uid);
        try {
            // Update Firebase Auth
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                    .setPhotoUrl(null);
            FirebaseAuth.getInstance().updateUser(request);
            Logger.d("Removed photoUrl from Firebase Auth for %s", uid);

            // Update Firestore
            Account account = accountRepository.getAccount(uid);
            if (account != null) {
                account.setProfileImageUrl(null);
                account.setUpdatedAt(Date.from(Instant.now()));
                accountRepository.updateAccount(account);
                Logger.d("Removed profileImageUrl from Firestore for %s", uid);
            } else {
                Logger.w("User account not found in Firestore for %s", uid);
                throw new Exception("User account not found.");
            }
        } catch (FirebaseAuthException e) {
            Logger.e("Firebase Auth error during pfp delete: %s", e.getMessage());
            throw new Exception("Failed to remove profile picture: " + e.getMessage());
        } catch (Exception e) {
            Logger.e("Unexpected error during pfp delete: %s", e.getMessage());
            throw new Exception("System error while removing profile picture.");
        }
    }
}
