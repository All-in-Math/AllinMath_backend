package com.allinmath.backend.endpoint;

import com.allinmath.backend.dto.student.EnrollTeacherDTO;
import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.ratelimit.RateLimit;
import com.allinmath.backend.ratelimit.RateLimitType;
import com.allinmath.backend.service.students.EnrollTeacherService;
import com.allinmath.backend.service.students.GetAllStudentsService;
import com.google.firebase.auth.FirebaseToken;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentsController {

    private final GetAllStudentsService studentsService;
    private final EnrollTeacherService enrollTeacherService;

    public StudentsController(GetAllStudentsService studentsService, EnrollTeacherService enrollTeacherService) {
        this.studentsService = studentsService;
        this.enrollTeacherService = enrollTeacherService;
    }

    @PostMapping("/get")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Map<String, List<Account>>> getStudents(@AuthenticationPrincipal FirebaseToken token) {
        List<Account> students = studentsService.getAll(token.getUid());
        return ResponseEntity.ok(Map.of("students", students));
    }

    @PostMapping("/enroll")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Map<String, String>> enrollTeacher(
            @AuthenticationPrincipal FirebaseToken token,
            @Valid @RequestBody EnrollTeacherDTO dto) {
        enrollTeacherService.enroll(token.getUid(), dto.getStudentId());
        return ResponseEntity.ok(Collections.singletonMap("message", "Teacher enrolled to student successfully"));
    }
}
