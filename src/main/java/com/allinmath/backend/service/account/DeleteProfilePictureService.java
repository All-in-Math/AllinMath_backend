package com.allinmath.backend.service.account;

import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.repository.account.AccountRepository;
import com.allinmath.backend.storage.AccountStorageService;
import com.allinmath.backend.util.Logger;
import com.google.cloud.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DeleteProfilePictureService {

    private final AccountRepository accountRepository;
    private final AccountStorageService storageService;

    public DeleteProfilePictureService(AccountRepository accountRepository, AccountStorageService storageService) {
        this.accountRepository = accountRepository;
        this.storageService = storageService;
    }

    public void delete(String uid) {
        Logger.i("Deleting profile picture for user: %s", uid);
        try {
            // 1. Delete from Storage (idempotent)
            storageService.deleteUserProfilePicture(uid);

            // 2. Update Firebase Auth (remove photo URL)
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                    .setPhotoUrl(null);
            FirebaseAuth.getInstance().updateUser(request);
            Logger.d("Removed photoUrl from Firebase Auth for %s", uid);

            // 3. Update Firestore
            Account account = accountRepository.getAccount(uid);
            if (account != null) {
                account.setProfileImageUrl(null);
                // FIXED: Using Timestamp instead of Date
                account.setUpdatedAt(Timestamp.now());
                accountRepository.updateAccount(account);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User account not found.");
            }
        } catch (Exception e) {
            Logger.e("Error deleting pfp: %s", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to remove profile picture.");
        }
    }
}