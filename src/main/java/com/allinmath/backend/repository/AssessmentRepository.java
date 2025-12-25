package com.allinmath.backend.repository;

import com.allinmath.backend.model.assessment.Assessment;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.Objects;

@Repository
public class AssessmentRepository {

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public String createAssessment(Assessment assessment) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("assessment").document();
        assessment.setId(docRef.getId());
        ApiFuture<WriteResult> result = docRef.set(assessment);
        result.get();
        return assessment.getId();
    }

    public List<Assessment> getAssessmentsByCourse(String courseId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getFirestore().collection("assessment")
                .whereArrayContains("courses", Objects.requireNonNull(courseId))
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Assessment> assessments = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            assessments.add(document.toObject(Assessment.class));
        }
        return assessments;
    }

    public Assessment getAssessment(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("assessment").document(Objects.requireNonNull(id));
        ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = docRef.get();
        com.google.cloud.firestore.DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(Assessment.class);
        } else {
            return null;
        }
    }

    public void updateAssessment(Assessment assessment) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("assessment").document(Objects.requireNonNull(assessment.getId()));
        ApiFuture<WriteResult> result = docRef.set(assessment);
        result.get();
    }

    public List<Assessment> getAssessmentsByCreator(String createdBy) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getFirestore().collection("assessment")
                .whereEqualTo("createdBy", Objects.requireNonNull(createdBy))
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Assessment> assessments = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            assessments.add(document.toObject(Assessment.class));
        }
        return assessments;
    }

    public void deleteAssessment(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("assessment").document(Objects.requireNonNull(id));
        ApiFuture<WriteResult> result = docRef.delete();
        result.get();
    }
}
