package com.allinmath.backend.repository;

import com.allinmath.backend.model.account.Account;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.Objects;

@Repository
public class StudentsRepository {

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public List<Account> getAllStudents(String teacherID) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documents = getFirestore().collection("account")
                .whereArrayContains("teacherIDs", Objects.requireNonNull(teacherID)).limit(20)
                .get().get().getDocuments();

        return documents.stream().map(doc -> doc.toObject(Account.class)).toList();
    }
}
