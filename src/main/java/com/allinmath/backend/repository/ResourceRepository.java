package com.allinmath.backend.repository;

import com.allinmath.backend.model.resource.Resource;
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
public class ResourceRepository {

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public String createResource(Resource resource) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("resource").document();
        resource.setId(docRef.getId());
        ApiFuture<WriteResult> result = docRef.set(resource);
        result.get();
        return resource.getId();
    }

    public List<Resource> getResourcesByCourse(String courseId) throws ExecutionException, InterruptedException {
        // Assuming 'attachedTo' contains the courseId
        ApiFuture<QuerySnapshot> future = getFirestore().collection("resource")
                .whereArrayContains("attachedTo", Objects.requireNonNull(courseId))
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Resource> resources = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            resources.add(document.toObject(Resource.class));
        }
        return resources;
    }

    public Resource getResource(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("resource").document(Objects.requireNonNull(id));
        ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = docRef.get();
        com.google.cloud.firestore.DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(Resource.class);
        } else {
            return null;
        }
    }

    public void updateResource(Resource resource) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("resource").document(Objects.requireNonNull(resource.getId()));
        ApiFuture<WriteResult> result = docRef.set(resource);
        result.get();
    }

    public void deleteResource(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("resource").document(Objects.requireNonNull(id));
        ApiFuture<WriteResult> result = docRef.delete();
        result.get();
    }
}
