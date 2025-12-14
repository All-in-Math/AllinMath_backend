package com.allinmath.backend.service.account;

import com.allinmath.backend.dto.account.ChangeNameDTO;
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
public class ChangeNameService {

    private final AccountRepository accountRepository;

    public ChangeNameService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void update(String uid, ChangeNameDTO dto) {
        String fullName = dto.getFirstName() + " " + dto.getLastName();

        try {
            // 1. Update Firebase Auth
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                    .setDisplayName(fullName);
            FirebaseAuth.getInstance().updateUser(request);

            // 2. Update Firestore
            Account account = accountRepository.getAccount(uid);
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User account not found.");
            }

            account.setFirstName(dto.getFirstName());
            account.setLastName(dto.getLastName());
            account.setUpdatedAt(Timestamp.now());

            accountRepository.updateAccount(account);

        } catch (Exception e) {
            Logger.e("Error changing name: %s", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update name.");
        }
    }
}