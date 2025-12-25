package com.allinmath.backend.repository;

import com.allinmath.backend.model.lesson.Lesson;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Repository
public class LessonRepository {
    private final Firestore firestore;
    private static final String COLLECTION_NAME = "lesson";

    public LessonRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Get lessons for a specific teacher on a specific date
     */
    public List<Lesson> getLessonsByTeacherAndDate(String teacherId, Timestamp startOfDay, Timestamp endOfDay)
            throws ExecutionException, InterruptedException {
        CollectionReference lessonsRef = firestore.collection(COLLECTION_NAME);

        Query query = lessonsRef
                .whereEqualTo("teacherId", Objects.requireNonNull(teacherId))
                .whereGreaterThanOrEqualTo("scheduledStartAt", Objects.requireNonNull(startOfDay))
                .whereLessThan("scheduledStartAt", Objects.requireNonNull(endOfDay))
                .whereIn("status", Objects.requireNonNull(List.of("scheduled", "in progress")));

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        List<Lesson> lessons = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Lesson lesson = document.toObject(Lesson.class);
            lessons.add(lesson);
        }

        return lessons;
    }

    /**
     * Get all lessons for a teacher
     */
    public List<Lesson> getLessonsByTeacher(String teacherId)
            throws ExecutionException, InterruptedException {
        CollectionReference lessonsRef = firestore.collection(COLLECTION_NAME);

        Query query = lessonsRef.whereEqualTo("teacherId", teacherId);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        List<Lesson> lessons = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Lesson lesson = document.toObject(Lesson.class);
            lessons.add(lesson);
        }

        return lessons;
    }

    /**
     * Get a lesson by ID
     */
    public Lesson getLessonById(String lessonId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(Objects.requireNonNull(lessonId));
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.toObject(Lesson.class);
        }
        return null;
    }

    /**
     * Create a new lesson
     */
    public String createLesson(Lesson lesson) throws ExecutionException, InterruptedException {
        lesson.setCreatedAt(Timestamp.now().toDate());
        lesson.setUpdatedAt(Timestamp.now().toDate());

        ApiFuture<DocumentReference> addedDocRef = firestore.collection(COLLECTION_NAME).add(lesson);
        DocumentReference docRef = addedDocRef.get();

        // Update the lesson with its ID
        lesson.setId(docRef.getId());
        docRef.set(lesson);

        return docRef.getId();
    }

    /**
     * Update an existing lesson
     */
    public void updateLesson(String lessonId, Map<String, Object> updates)
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(Objects.requireNonNull(lessonId));

        updates.put("updatedAt", Timestamp.now());

        ApiFuture<WriteResult> writeResult = docRef.update(updates);
        writeResult.get();
    }

    /**
     * Delete a lesson
     */
    public void deleteLesson(String lessonId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(Objects.requireNonNull(lessonId));
        ApiFuture<WriteResult> writeResult = docRef.delete();
        writeResult.get();
    }
}
