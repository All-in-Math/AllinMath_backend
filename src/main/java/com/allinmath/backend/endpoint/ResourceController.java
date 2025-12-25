package com.allinmath.backend.endpoint;

import com.allinmath.backend.dto.resource.CreateResourceDTO;
import com.allinmath.backend.dto.resource.GetResourcesDTO;
import com.allinmath.backend.model.resource.Resource;
import com.allinmath.backend.ratelimit.RateLimit;
import com.allinmath.backend.ratelimit.RateLimitType;
import com.allinmath.backend.service.resource.ResourceService;
import com.google.firebase.auth.FirebaseToken;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping("/create")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Map<String, String>> createResource(
            @AuthenticationPrincipal FirebaseToken token,
            @Valid @RequestBody CreateResourceDTO dto) {
        String id = resourceService.createResource(dto, token.getUid());
        return ResponseEntity.ok(Collections.singletonMap("id", id));
    }

    @PostMapping("/list")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Map<String, List<Resource>>> getResourcesByCourse(
            @AuthenticationPrincipal FirebaseToken token,
            @Valid @RequestBody GetResourcesDTO dto) {
        List<Resource> resources = resourceService.getResourcesByCourse(dto.getCourseName());
        return ResponseEntity.ok(Collections.singletonMap("resources", resources));
    }

    @PostMapping("/update")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Map<String, String>> updateResource(
            @AuthenticationPrincipal FirebaseToken token,
            @Valid @RequestBody com.allinmath.backend.dto.resource.UpdateResourceDTO dto) {
        resourceService.updateResource(dto);
        return ResponseEntity.ok(Collections.singletonMap("message", "Resource updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    @RateLimit(type = RateLimitType.DEFAULT)
    public ResponseEntity<Map<String, String>> deleteResource(
            @AuthenticationPrincipal FirebaseToken token,
            @PathVariable String id) {
        resourceService.deleteResource(id, token.getUid());
        return ResponseEntity.ok(Collections.singletonMap("message", "Resource deleted successfully"));
    }
}
