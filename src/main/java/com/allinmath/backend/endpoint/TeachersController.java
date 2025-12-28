package com.allinmath.backend.endpoint;

import com.allinmath.backend.dto.teacher.SearchTeachersDTO;
import com.allinmath.backend.model.account.TeacherProfile;
import com.allinmath.backend.ratelimit.RateLimit;
import com.allinmath.backend.ratelimit.RateLimitType;
import com.allinmath.backend.service.teacher.TeacherSearchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teachers")
public class TeachersController {

    private final TeacherSearchService teacherSearchService;

    public TeachersController(TeacherSearchService teacherSearchService) {
        this.teacherSearchService = teacherSearchService;
    }

    @PostMapping("/search")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Map<String, List<TeacherProfile>>> searchTeachers(@Valid @RequestBody SearchTeachersDTO dto) {
        List<TeacherProfile> teachers = teacherSearchService.search(dto);
        return ResponseEntity.ok(Collections.singletonMap("teachers", teachers));
    }
}
