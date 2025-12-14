package com.allinmath.backend.endpoint;

import com.allinmath.backend.dto.account.ChangeNameDTO;
import com.allinmath.backend.dto.account.SendPasswordResetEmailDTO;
import com.allinmath.backend.dto.account.SignUpDTO;
import com.allinmath.backend.security.FirebaseAuthenticationToken;
import com.allinmath.backend.service.account.*;
import com.google.firebase.auth.FirebaseToken;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final RegisterService registerService;
    private final VerifyEmailService verifyEmailService;
    private final UpdateProfilePictureService updateProfilePictureService;
    private final DeleteProfilePictureService deleteProfilePictureService;
    private final ChangeNameService changeNameService;
    private final SendPasswordResetEmailService sendPasswordResetEmailService;

    public AccountController(
            RegisterService registerService,
            VerifyEmailService verifyEmailService,
            UpdateProfilePictureService updateProfilePictureService,
            DeleteProfilePictureService deleteProfilePictureService,
            ChangeNameService changeNameService,
            SendPasswordResetEmailService sendPasswordResetEmailService
    ) {
        this.registerService = registerService;
        this.verifyEmailService = verifyEmailService;
        this.updateProfilePictureService = updateProfilePictureService;
        this.deleteProfilePictureService = deleteProfilePictureService;
        this.changeNameService = changeNameService;
        this.sendPasswordResetEmailService = sendPasswordResetEmailService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody SignUpDTO dto) {
        String signInToken = registerService.register(dto);
        Map<String, String> response = new HashMap<>();
        response.put("msg", "User registered successfully");
        response.put("signInToken", signInToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verifyEmail")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @AuthenticationPrincipal FirebaseAuthenticationToken authentication) {
        FirebaseToken token = (FirebaseToken) authentication.getPrincipal();
        verifyEmailService.verify(token.getUid());
        return ResponseEntity.ok(Collections.singletonMap("message", "Verification email sent"));
    }

    // FIXED: Now consumes multipart/form-data correctly
    @PostMapping(value = "/pfp/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> updateProfilePicture(
            @AuthenticationPrincipal FirebaseAuthenticationToken authentication) {

        FirebaseToken token = (FirebaseToken) authentication.getPrincipal();
        // Passed file to service
        updateProfilePictureService.update(token.getUid());

        return ResponseEntity.ok(Collections.singletonMap("message", "Profile picture updated"));
    }

    @DeleteMapping("/pfp/delete")
    public ResponseEntity<Map<String, String>> deleteProfilePicture(
            @AuthenticationPrincipal FirebaseAuthenticationToken authentication) {
        FirebaseToken token = (FirebaseToken) authentication.getPrincipal();
        deleteProfilePictureService.delete(token.getUid());
        return ResponseEntity.ok(Collections.singletonMap("message", "Profile picture deleted"));
    }

    @PutMapping("/name/change")
    public ResponseEntity<Map<String, String>> changeName(
            @AuthenticationPrincipal FirebaseAuthenticationToken authentication,
            @Valid @RequestBody ChangeNameDTO dto) {
        FirebaseToken token = (FirebaseToken) authentication.getPrincipal();
        changeNameService.update(token.getUid(), dto);
        return ResponseEntity.ok(Collections.singletonMap("message", "Name updated successfully"));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, String>> sendPasswordResetEmail(
            @Valid @RequestBody SendPasswordResetEmailDTO dto) {
        try {
            sendPasswordResetEmailService.send(dto);
        } catch (Exception e) {
            // Logged internally, suppress error to user for security
        }
        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset email sent (if user exists)"));
    }
}