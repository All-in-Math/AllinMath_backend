package com.allinmath.backend.service.students;

import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.model.account.StudentProfile;
import com.allinmath.backend.model.account.TeacherProfile;
import com.allinmath.backend.repository.AccountRepository;
import com.allinmath.backend.util.Logger;
import com.google.cloud.Timestamp;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EnrollTeacherService {

    private final AccountRepository accountRepository;

    public EnrollTeacherService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void enroll(String teacherId, String studentId) {
        try {
            long startTime = System.currentTimeMillis();
            Logger.i("Enrolling teacher %s to student %s", teacherId, studentId);

            // Validate that the requesting user is a teacher
            Account teacherAccount = accountRepository.getAccount(teacherId);
            if (teacherAccount == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher account not found.");
            }
            if (!(teacherAccount instanceof TeacherProfile)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only teachers can enroll to students.");
            }

            // Get the student account
            Account account = accountRepository.getAccount(studentId);
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
            }

            // Ensure the account is a student profile
            if (!(account instanceof StudentProfile)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is not a student.");
            }

            StudentProfile studentProfile = (StudentProfile) account;

            // Initialize teacherIDs list if null
            if (studentProfile.getTeacherIDs() == null) {
                studentProfile.setTeacherIDs(new java.util.ArrayList<>());
            }

            // Add teacher ID to the student's teacherIDs list
            studentProfile.addTeacherID(teacherId);
            studentProfile.setUpdatedAt(Timestamp.now());

            // Update the student account in Firestore
            accountRepository.updateAccount(studentProfile);

            long endTime = System.currentTimeMillis();
            Logger.i("Enrolled teacher %s to student %s in %d ms", teacherId, studentId, endTime - startTime);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            Logger.e("Error enrolling teacher %s to student %s: %s", teacherId, studentId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to enroll teacher to student.");
        }
    }
}
