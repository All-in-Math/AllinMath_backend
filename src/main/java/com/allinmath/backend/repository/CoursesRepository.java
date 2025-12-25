package com.allinmath.backend.repository;

import com.allinmath.backend.model.account.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.Objects;

@Repository
public class CoursesRepository {

    private Map<String, Map<String, List<String>>> subjectsMap;

    @PostConstruct
    public void init() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("tyt_ayt_subjects.json");
            subjectsMap = mapper.readValue(resource.getInputStream(),
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Map<String, List<String>>>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    // Get all student IDs in a course from the teacherID
    public List<Account> getStudentsInCourse(String courseName, String teacherID)
            throws ExecutionException, InterruptedException {
        List<Account> students = new ArrayList<>();
        String requiredSections = null;

        if (subjectsMap != null) {
            if (subjectsMap.containsKey("TYT") && subjectsMap.get("TYT").containsKey(courseName)) {
                requiredSections = "TYT";
            } else if (subjectsMap.containsKey("AYT") && subjectsMap.get("AYT").containsKey(courseName)) {
                requiredSections = "AYT";
            }
        }

        assert requiredSections != null;

        // Query for students who have this teacher and this course
        // Note: Firestore only allows one array-contains filter.
        // We filter by teacherIDs at DB level and sections in memory.
        List<QueryDocumentSnapshot> documents = getFirestore().collection("account")
                .whereArrayContains("teacherIDs", Objects.requireNonNull(teacherID)).limit(20)
                .get().get().getDocuments();

        final String finalRequiredSections = requiredSections;
        com.allinmath.backend.util.Logger.i("Processing %d documents for course query", documents.size());

        students = documents.stream()
                .map(doc -> {
                    try {
                        return (Account) doc.toObject(com.allinmath.backend.model.account.StudentProfile.class);
                    } catch (Exception e) {
                        com.allinmath.backend.util.Logger.e("Mapping error for doc %s: %s", doc.getId(),
                                e.getMessage());
                        return null;
                    }
                })
                .filter(account -> account != null && account.getSections() != null
                        && account.getSections().contains(finalRequiredSections))
                .toList();

        return students;
    }

    public void updateTeacherCourses(String uid, java.util.List<String> tytCourses, java.util.List<String> aytCourses)
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = getFirestore().collection("account").document(Objects.requireNonNull(uid));
        ApiFuture<WriteResult> result = docRef.update(
                "tytCourses", tytCourses,
                "aytCourses", aytCourses,
                "updatedAt", com.google.cloud.Timestamp.now());
        result.get();
    }
}
