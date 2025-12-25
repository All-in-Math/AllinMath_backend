package com.allinmath.backend.service.courses;

import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.repository.CoursesRepository;
import com.allinmath.backend.util.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class GetStudentsInCourse {

   private final CoursesRepository courseRepository;

   public GetStudentsInCourse(CoursesRepository courseRepository) {
       this.courseRepository = courseRepository;
   }

   public List<Account> get(String courseName, String teacherID) {
       try {
           // Start timer
           long startTime = System.currentTimeMillis();
           Logger.i("Fetching students for course: %s and teacherID: %s", courseName, teacherID);
           List<Account> studentIDs = courseRepository.getStudentsInCourse(courseName, teacherID);
           if (studentIDs == null) {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No students found for the course.");
           }
           long endTime = System.currentTimeMillis();
           Logger.i("Fetched %d students in %d ms", studentIDs.size(), (endTime - startTime));
           return studentIDs;
       } catch (ExecutionException | InterruptedException e) {
           Logger.e(e, "Execution error fetching students for course: %s and teacherID: %s", courseName, teacherID);
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch students for the course.");
       } catch (IllegalArgumentException e) {
           Logger.e(e, "Invalid argument fetching students for course: %s and teacherID: %s", courseName, teacherID);
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
       } catch (Exception e) {
           Logger.e(e, "Error fetching students for course: %s and teacherID: %s", courseName, teacherID);
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch students for the course.");
       }
   }
}