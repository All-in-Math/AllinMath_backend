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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UpdateProfilePictureService {

    private final AccountRepository accountRepository;
    private final AccountStorageService storageService;

    public UpdateProfilePictureService(AccountRepository accountRepository, AccountStorageService storageService) {
        this.accountRepository = accountRepository;
        this.storageService = storageService;
    }

    public void update(String uid) {
        Logger.i("Updating profile picture for user: %s", uid);
        try {
            // Get the signed URL
            String photoUrl = storageService.getUserProfilePictureUrl(uid);

            // Update Firebase Auth (so it shows in simple auth checks)
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                    .setPhotoUrl(photoUrl);
            FirebaseAuth.getInstance().updateUser(request);

            // Update Firestore
            Account account = accountRepository.getAccount(uid);
            if (account != null) {
                account.setProfileImageUrl(photoUrl);
                account.setUpdatedAt(Timestamp.now());
                accountRepository.updateAccount(account);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User account not found.");
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            Logger.e("Error updating pfp: %s", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update profile picture.");
        }
    }
}