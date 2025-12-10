package com.allinmath.backend.service.account;

import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.repository.account.AccountRepository;
import com.allinmath.backend.util.Logger;
import com.allinmath.backend.storage.AccountStorageService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Service
public class UpdateProfilePictureService {

    private final AccountRepository accountRepository;
    private final AccountStorageService storageService;

    public UpdateProfilePictureService(AccountRepository accountRepository, AccountStorageService storageService) {
        this.accountRepository = accountRepository;
        this.storageService = storageService;
    }

    public void update(String uid) throws Exception {
        Logger.i("Updating profile picture for user: %s", uid);
        try {
            // Get the photo URL from the Firebase storage bucket
            String photoUrl = storageService.getUserProfilePictureUrl(uid);

            // Update Firebase Auth
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                    .setPhotoUrl(photoUrl);
            FirebaseAuth.getInstance().updateUser(request);
            Logger.d("Updated photoUrl in Firebase Auth for %s", uid);

            // Update Firestore
            Account account = accountRepository.getAccount(uid);
            if (account != null) {
                account.setProfileImageUrl(photoUrl);
                account.setUpdatedAt(Date.from(Instant.now()));
                accountRepository.updateAccount(account);
                Logger.d("Updated profileImageUrl in Firestore for %s", uid);
            } else {
                 Logger.w("User account not found in Firestore for %s", uid);
                 throw new Exception("User account found in Auth but not in Database.");
            }
        } catch (FirebaseAuthException e) {
             Logger.e("Firebase Auth error during pfp update: %s", e.getMessage());
             throw new Exception("Failed to update profile picture in authentication provider: " + e.getMessage());
        } catch (ExecutionException | InterruptedException e) {
             Logger.e("Firestore error during pfp update: %s", e.getMessage());
             throw new Exception("Database error while updating profile picture.");
        } catch (Exception e) {
             Logger.e("Unexpected error during pfp update: %s", e.getMessage());
             throw new Exception("An error occurred while updating profile picture: " + e.getMessage());
        }
    }
}
