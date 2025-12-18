package com.allinmath.backend.repository.account;

import com.allinmath.backend.model.account.Account;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository
public class AccountRepository {

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public void createAccount(Account account) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("account").document(account.getUid());
        ApiFuture<WriteResult> result = docRef.set(account);
        result.get();
    }

    public Account getAccount(String uid) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("account").document(uid);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.toObject(Account.class);
        } else {
            return null;
        }
    }

    public void updateAccount(Account account) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("account").document(account.getUid());
        ApiFuture<WriteResult> result = docRef.set(account);
        result.get();
    }

    public void deleteAccount(String uid) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("account").document(uid);
        ApiFuture<WriteResult> result = docRef.delete();
        result.get();
    }
}