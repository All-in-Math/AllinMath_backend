package com.allinmath.backend.service.students;

import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.repository.StudentsRepository;
import com.allinmath.backend.util.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class GetAllStudentsService {

    private final StudentsRepository studentsRepository;

    public GetAllStudentsService(StudentsRepository courseRepository) {
        this.studentsRepository = courseRepository;
    }

    public List<Account> getAll(String uid) {
        try {
            long startTime = System.currentTimeMillis();
            Logger.i("Getting students for teacher: %s", uid);
            List<Account> students = studentsRepository.getAllStudents(uid);

            long endTime = System.currentTimeMillis();
            Logger.i("Got students for teacher: %s in %d ms", uid, endTime - startTime);
            return students;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get students.");
        }
    }
}
