package com.allinmath.backend.repository;

import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.model.account.StudentProfile;
import com.allinmath.backend.model.account.TeacherProfile;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;
import java.util.Objects;

@Repository
public class AccountRepository {

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public void createAccount(Account account) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("account")
                .document(Objects.requireNonNull(account.getUid()));
        ApiFuture<WriteResult> result = docRef.set(account);
        result.get();
    }

    public Account getAccount(String uid) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("account").document(Objects.requireNonNull(uid));
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            String role = document.getString("role");
            if (role != null && role.equalsIgnoreCase("TEACHER")) {
                return document.toObject(TeacherProfile.class);
            } else if (role != null && role.equalsIgnoreCase("STUDENT")) {
                return document.toObject(StudentProfile.class);
            }
            return document.toObject(Account.class);
        } else {
            return null;
        }
    }

    public void updateAccount(Account account) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("account")
                .document(Objects.requireNonNull(account.getUid()));
        ApiFuture<WriteResult> result = docRef.set(account);
        result.get();
    }

    public void batchCreateAccounts(java.util.List<Account> accounts) throws ExecutionException, InterruptedException {
        com.google.cloud.firestore.WriteBatch batch = getFirestore().batch();
        for (Account account : accounts) {
            DocumentReference docRef = getFirestore().collection("account")
                    .document(Objects.requireNonNull(account.getUid()));
            batch.set(docRef, account);
        }
        batch.commit().get();
    }

    public void deleteAccount(String uid) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("account").document(Objects.requireNonNull(uid));
        ApiFuture<WriteResult> result = docRef.delete();
        result.get();
    }
}
