package com.allinmath.backend.endpoint;

import com.allinmath.backend.dto.assessment.CreateAssessmentDTO;
import com.allinmath.backend.dto.assessment.GetAssessmentsDTO;
import com.allinmath.backend.model.assessment.Assessment;
import com.allinmath.backend.ratelimit.RateLimit;
import com.allinmath.backend.ratelimit.RateLimitType;
import com.allinmath.backend.service.assessment.AssessmentService;
import com.google.firebase.auth.FirebaseToken;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/assessments")
public class AssessmentsController {

    private final AssessmentService assessmentService;

    public AssessmentsController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping("/create")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Map<String, String>> createAssessment(
            @AuthenticationPrincipal FirebaseToken token,
            @Valid @RequestBody CreateAssessmentDTO dto) {
        String id = assessmentService.createAssessment(dto, token.getUid());
        return ResponseEntity.ok(Collections.singletonMap("id", id));
    }

    @PostMapping("/list")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Map<String, List<Assessment>>> getAssessmentsByCourse(
            @AuthenticationPrincipal FirebaseToken token,
            @Valid @RequestBody GetAssessmentsDTO dto) {
        List<Assessment> assessments = assessmentService.getAssessmentsByCourse(dto.getCourseName(), dto.getType());
        return ResponseEntity.ok(Collections.singletonMap("assessments", assessments));
    }

    @PostMapping("/list-all")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Map<String, List<Assessment>>> getAssessmentsByCreator(
            @AuthenticationPrincipal FirebaseToken token,
            @RequestBody Map<String, String> body) {
        String type = body.get("type");
        List<Assessment> assessments = assessmentService.getAssessmentsByCreator(token.getUid(), type);
        return ResponseEntity.ok(Collections.singletonMap("assessments", assessments));
    }

    @PostMapping("/update")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Map<String, String>> updateAssessment(
            @AuthenticationPrincipal FirebaseToken token,
            @Valid @RequestBody com.allinmath.backend.dto.assessment.UpdateAssessmentDTO dto) {
        assessmentService.updateAssessment(dto);
        return ResponseEntity.ok(Collections.singletonMap("message", "Assessment updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Map<String, String>> deleteAssessment(
            @AuthenticationPrincipal FirebaseToken token,
            @PathVariable String id) {
        assessmentService.deleteAssessment(id, token.getUid());
        return ResponseEntity.ok(Collections.singletonMap("message", "Assessment deleted successfully"));
    }
}