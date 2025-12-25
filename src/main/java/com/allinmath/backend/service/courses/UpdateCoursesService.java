package com.allinmath.backend.service.courses;

import com.allinmath.backend.dto.courses.UpdateCoursesDTO;
import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.model.account.TeacherProfile;
import com.allinmath.backend.repository.AccountRepository;
import com.allinmath.backend.repository.CoursesRepository;
import com.allinmath.backend.util.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;

@Service
public class UpdateCoursesService {

    private final AccountRepository accountRepository;
    private final CoursesRepository coursesRepository;

    public UpdateCoursesService(AccountRepository accountRepository, CoursesRepository coursesRepository) {
        this.accountRepository = accountRepository;
        this.coursesRepository = coursesRepository;
    }

    public void update(String uid, UpdateCoursesDTO dto) {
        try {
            // Start timer
            long startTime = System.currentTimeMillis();
            Logger.i("Updating courses for user: %s", uid);
            Account account = accountRepository.getAccount(uid);
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
            }

            if (!(account instanceof TeacherProfile teacher)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is not a teacher profile");
            }

            coursesRepository.updateTeacherCourses(teacher.getUid(), dto.getTytCourses(), dto.getAytCourses());

            // Stop timer
            long endTime = System.currentTimeMillis();
            Logger.i("Courses updated for user: %s in %s", uid, endTime - startTime);

        } catch (ExecutionException | InterruptedException e) {
            Logger.e("Error updating courses for %s: %s", uid, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update courses");
        }
    }
}
