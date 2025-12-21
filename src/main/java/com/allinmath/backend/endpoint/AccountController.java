package com.allinmath.backend.endpoint;

import com.allinmath.backend.dto.account.ChangeEmailDTO;
import com.allinmath.backend.dto.account.ChangeNameDTO;
import com.allinmath.backend.dto.account.SendPasswordResetEmailDTO;
import com.allinmath.backend.dto.account.SignUpDTO;
import com.allinmath.backend.dto.account.CompleteOnboardingDTO;
import com.allinmath.backend.service.account.*;
import com.google.firebase.auth.FirebaseToken;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final RegisterService registerService;
    private final VerificationEmailService verifyEmailService;
    private final UpdateProfilePictureService updateProfilePictureService;
    private final DeleteProfilePictureService deleteProfilePictureService;
    private final ChangeNameService changeNameService;
    private final SendPasswordResetEmailService sendPasswordResetEmailService;
    private final ChangeEmailService changeEmailService;
    private final CompleteOnboardingService completeOnboardingService;

    public AccountController(
            RegisterService registerService,
            VerificationEmailService verifyEmailService,
            UpdateProfilePictureService updateProfilePictureService,
            DeleteProfilePictureService deleteProfilePictureService,
            ChangeNameService changeNameService,
            SendPasswordResetEmailService sendPasswordResetEmailService,
            ChangeEmailService changeEmailService,
            CompleteOnboardingService completeOnboardingService
    ) {
        this.registerService = registerService;
        this.verifyEmailService = verifyEmailService;
        this.updateProfilePictureService = updateProfilePictureService;
        this.deleteProfilePictureService = deleteProfilePictureService;
        this.changeNameService = changeNameService;
        this.sendPasswordResetEmailService = sendPasswordResetEmailService;
        this.changeEmailService = changeEmailService;
        this.completeOnboardingService = completeOnboardingService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody SignUpDTO dto) {
        String signInToken = registerService.register(dto);
        Map<String, String> response = new HashMap<>();
        response.put("msg", "User registered successfully");
        response.put("signInToken", signInToken);
        return ResponseEntity.ok(response);
    }

    @PutMapping("email/verify/sendEmail")
    public ResponseEntity<Map<String, String>> sendVerifyEmail(
            @AuthenticationPrincipal FirebaseToken token) {
        verifyEmailService.send(token.getUid());
        return ResponseEntity.ok(Collections.singletonMap("message", "Verification email sent"));
    }

    @PutMapping("/email/change")
    public ResponseEntity<Map<String, String>> changeEmail(
            @AuthenticationPrincipal FirebaseToken token,
            @Valid @RequestBody ChangeEmailDTO dto) {
        changeEmailService.change(token.getUid(), dto.getCurrentEmail(), dto.getNewEmail());
        return ResponseEntity.ok(Collections.singletonMap("message", "Email changed successfully"));
    }

    @PostMapping("/pfp/update")
    public ResponseEntity<Map<String, String>> updateProfilePicture(
            @AuthenticationPrincipal FirebaseToken token) {

        updateProfilePictureService.update(token.getUid());

        return ResponseEntity.ok(Collections.singletonMap("message", "Profile picture updated"));
    }

    @DeleteMapping("/pfp/delete")
    public ResponseEntity<Map<String, String>> deleteProfilePicture(
            @AuthenticationPrincipal FirebaseToken token) {
        deleteProfilePictureService.delete(token.getUid());
        return ResponseEntity.ok(Collections.singletonMap("message", "Profile picture deleted"));
    }

    @PutMapping("/name/change")
    public ResponseEntity<Map<String, String>> changeName(
            @AuthenticationPrincipal FirebaseToken token,
            @Valid @RequestBody ChangeNameDTO dto) {
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

    @PostMapping("/onboarding/complete")
    public ResponseEntity<Map<String, String>> completeOnboarding(
            @AuthenticationPrincipal FirebaseToken token,
            @Valid @RequestBody CompleteOnboardingDTO dto) {
        completeOnboardingService.complete(token.getUid(), dto);
        return ResponseEntity.ok(Collections.singletonMap("message", "Onboarding completed successfully"));
    }
}