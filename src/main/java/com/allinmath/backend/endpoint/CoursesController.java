package com.allinmath.backend.endpoint;

import com.allinmath.backend.dto.courses.GetStudentsFromCourseDTO;
import com.allinmath.backend.dto.courses.UpdateCoursesDTO;
import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.ratelimit.RateLimit;
import com.allinmath.backend.ratelimit.RateLimitType;
import com.allinmath.backend.service.courses.GetStudentsInCourse;
import com.allinmath.backend.service.courses.UpdateCoursesService;
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
@RequestMapping("/courses")
public class CoursesController {

    private final GetStudentsInCourse coursesService;
    private final UpdateCoursesService updateCoursesService;

    public CoursesController(GetStudentsInCourse coursesService, UpdateCoursesService updateCoursesService) {
        this.coursesService = coursesService;
        this.updateCoursesService = updateCoursesService;
    }

    @PostMapping("/getStudents")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Map<String, List<Account>>> getStudents(@AuthenticationPrincipal FirebaseToken token,
            @Valid @RequestBody GetStudentsFromCourseDTO dto) {
        List<Account> students = coursesService.get(dto.getCourseName(), dto.getTeacherID());
        return ResponseEntity.ok(Collections.singletonMap("students", students));
    }

    @PostMapping("/update")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Void> updateCourses(@AuthenticationPrincipal FirebaseToken token,
            @Valid @RequestBody UpdateCoursesDTO dto) {
        updateCoursesService.update(token.getUid(), dto);
        return ResponseEntity.ok().build();
    }
}