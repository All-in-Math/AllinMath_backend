package com.allinmath.backend.service.account;

import com.allinmath.backend.dto.account.ChangeNameDTO;
import com.allinmath.backend.dto.account.SendPasswordResetEmailDTO;
import com.allinmath.backend.dto.account.SignUpDTO;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.concurrent.ExecutionException;

public interface AccountService {
    void register(SignUpDTO dto) throws FirebaseAuthException, ExecutionException, InterruptedException;
    void updateProfilePicture(String uid, String photoUrl) throws FirebaseAuthException, ExecutionException, InterruptedException;
    void deleteProfilePicture(String uid) throws FirebaseAuthException, ExecutionException, InterruptedException;
    void changeName(String uid, ChangeNameDTO dto) throws FirebaseAuthException, ExecutionException, InterruptedException;
    void sendPasswordResetEmail(SendPasswordResetEmailDTO dto) throws FirebaseAuthException;
}
