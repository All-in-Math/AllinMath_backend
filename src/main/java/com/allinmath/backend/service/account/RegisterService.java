package com.allinmath.backend.service.account;

import com.allinmath.backend.dto.account.SignUpDTO;
import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.repository.account.AccountRepository;
import com.allinmath.backend.util.Logger;
import com.google.cloud.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegisterService {

    private final AccountRepository accountRepository;

    public RegisterService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String register(SignUpDTO dto) {
        Logger.i("Attempting to register new user: %s", dto.getEmail());
        UserRecord userRecord = null;

        try {
            // Create User in Firebase Auth
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(dto.getEmail())
                    .setPassword(dto.getPassword())
                    .setDisplayName(dto.getFirstName() + " " + dto.getLastName());

            userRecord = FirebaseAuth.getInstance().createUser(request);

            // Create Custom Token
            String customToken = FirebaseAuth.getInstance().createCustomToken(userRecord.getUid());

            // Create Account in Firestore
            Account account = new Account();
            account.setUid(userRecord.getUid());
            account.setEmail(dto.getEmail());
            account.setFirstName(dto.getFirstName());
            account.setLastName(dto.getLastName());
            account.setCreatedAt(Timestamp.now());
            account.setUpdatedAt(Timestamp.now());
            account.setEnabled(true);

            accountRepository.createAccount(account);
            Logger.i("Registration successful for: %s", dto.getEmail());

            return customToken;

        } catch (FirebaseAuthException e) {
            Logger.i("Firebase Auth Error: %s", e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists or invalid data.");
        } catch (Exception e) {
            Logger.e("Registration failed: %s", e.getMessage());

            // ROLLBACK: Delete the Auth user if Firestore save fails
            if (userRecord != null) {
                try {
                    FirebaseAuth.getInstance().deleteUser(userRecord.getUid());
                    Logger.w("Rolled back orphaned Auth user: %s", userRecord.getUid());
                } catch (FirebaseAuthException ex) {
                    Logger.e("Failed to rollback user: %s", ex.getMessage());
                }
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed. Please try again.");
        }
    }
}