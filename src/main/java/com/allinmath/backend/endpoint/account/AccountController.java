package com.allinmath.backend.endpoint.account;

import com.allinmath.backend.dto.account.ChangeNameDTO;
import com.allinmath.backend.dto.account.SendPasswordResetEmailDTO;
import com.allinmath.backend.dto.account.SignUpDTO;
import com.allinmath.backend.security.FirebaseAuthenticationToken;
import com.allinmath.backend.service.account.*;
import com.allinmath.backend.util.Logger;
import com.google.firebase.auth.FirebaseToken;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    private final UpdateProfilePictureService updateProfilePictureService;
    private final DeleteProfilePictureService deleteProfilePictureService;
    private final ChangeNameService changeNameService;
    private final SendPasswordResetEmailService sendPasswordResetEmailService;

    public AccountController(
        RegisterService registerService,
        UpdateProfilePictureService updateProfilePictureService,
        DeleteProfilePictureService deleteProfilePictureService,
        ChangeNameService changeNameService,
        SendPasswordResetEmailService sendPasswordResetEmailService
    ) {
        this.registerService = registerService;
        this.updateProfilePictureService = updateProfilePictureService;
        this.deleteProfilePictureService = deleteProfilePictureService;
        this.changeNameService = changeNameService;
        this.sendPasswordResetEmailService = sendPasswordResetEmailService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody SignUpDTO dto) {
        try {
            String signInToken = registerService.register(dto);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("signInToken", signInToken);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.e("Register Endpoint Error: %s", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/pfp/update")
    public ResponseEntity<Map<String, String>> updateProfilePicture(
            @AuthenticationPrincipal FirebaseAuthenticationToken authentication,
            @RequestBody Map<String, String> body) {
        try {
            String photoUrl = body.get("photoUrl");
            if (photoUrl == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "photoUrl is required"));
            }
            
            FirebaseToken token = (FirebaseToken) authentication.getPrincipal();
            updateProfilePictureService.update(token.getUid(), photoUrl);
            return ResponseEntity.ok(Collections.singletonMap("message", "Profile picture updated"));
        } catch (Exception e) {
            Logger.e("Update PFP Error: %s", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/pfp/delete")
    public ResponseEntity<Map<String, String>> deleteProfilePicture(@AuthenticationPrincipal FirebaseAuthenticationToken authentication) {
        try {
            FirebaseToken token = (FirebaseToken) authentication.getPrincipal();
            deleteProfilePictureService.execute(token.getUid());
            return ResponseEntity.ok(Collections.singletonMap("message", "Profile picture deleted"));
        } catch (Exception e) {
            Logger.e("Delete PFP Error: %s", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/name/change")
    public ResponseEntity<Map<String, String>> changeName(
            @AuthenticationPrincipal FirebaseAuthenticationToken authentication,
            @Valid @RequestBody ChangeNameDTO dto) {
        try {
            FirebaseToken token = (FirebaseToken) authentication.getPrincipal();
            changeNameService.execute(token.getUid(), dto);
            return ResponseEntity.ok(Collections.singletonMap("message", "Name updated successfully"));
        } catch (Exception e) {
            Logger.e("Change Name Error: %s", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, String>> sendPasswordResetEmail(@Valid @RequestBody SendPasswordResetEmailDTO dto) {
        try {
            sendPasswordResetEmailService.execute(dto);
            return ResponseEntity.ok(Collections.singletonMap("message", "Password reset email sent (if user exists)"));
        } catch (Exception e) {
            Logger.e("Reset Password Error: %s", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
