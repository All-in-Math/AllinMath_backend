package com.allinmath.backend.repository;

import com.allinmath.backend.dto.teacher.SearchTeachersDTO;
import com.allinmath.backend.model.account.TeacherProfile;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class TeachersRepository {

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public List<TeacherProfile> searchTeachers(SearchTeachersDTO dto) throws ExecutionException, InterruptedException {
        CollectionReference accounts = getFirestore().collection("account");
        Query query = accounts.whereEqualTo("role", "TEACHER");

        // Firestore has limitations on multiple inequality filters on different fields.
        // We will apply basic filters here and more complex ones in memory if needed,
        // or just try to build a valid query.

        if (dto.getMinPrice() != null) {
            query = query.whereGreaterThanOrEqualTo("hourlyRate", dto.getMinPrice().doubleValue());
        }
        if (dto.getMaxPrice() != null) {
            query = query.whereLessThanOrEqualTo("hourlyRate", dto.getMaxPrice().doubleValue());
        }
        if (dto.getMinRating() != null) {
            query = query.whereGreaterThanOrEqualTo("rating", dto.getMinRating().doubleValue());
        }

        // For courses and subjects, Firestore doesn't support multiple array-contains
        // in one query.
        // We'll fetch and filter in memory for those if multiple are provided,
        // or just use one if only one is provided.

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        List<TeacherProfile> teachers = documents.stream()
                .map(doc -> doc.toObject(TeacherProfile.class))
                .collect(Collectors.toList());

        // In-memory filtering for more complex logic
        return teachers.stream()
                .filter(t -> filterBySubjectAndCourses(t, dto))
                .collect(Collectors.toList());
    }

    private boolean filterBySubjectAndCourses(TeacherProfile teacher, SearchTeachersDTO dto) {
        if (dto.getSubject() != null && !dto.getSubject().isEmpty()) {
            boolean matchesTyt = teacher.getTytCourses() != null && teacher.getTytCourses().contains(dto.getSubject());
            boolean matchesAyt = teacher.getAytCourses() != null && teacher.getAytCourses().contains(dto.getSubject());
            if (!matchesTyt && !matchesAyt)
                return false;
        }

        if (dto.getCourses() != null && !dto.getCourses().isEmpty()) {
            boolean matchesAny = false;
            for (String course : dto.getCourses()) {
                if ((teacher.getTytCourses() != null && teacher.getTytCourses().contains(course)) ||
                        (teacher.getAytCourses() != null && teacher.getAytCourses().contains(course))) {
                    matchesAny = true;
                    break;
                }
            }
            if (!matchesAny)
                return false;
        }

        return true;
    }
}
