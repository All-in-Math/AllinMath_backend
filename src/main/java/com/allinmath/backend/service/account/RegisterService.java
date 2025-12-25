package com.allinmath.backend.service.account;

import com.allinmath.backend.dto.account.SignUpDTO;
import com.allinmath.backend.model.account.Account;
import com.allinmath.backend.model.account.AuthMeta;
import com.allinmath.backend.repository.AccountRepository;
import com.allinmath.backend.util.Logger;
import com.google.cloud.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;

@Service
public class RegisterService {

    private final AccountRepository accountRepository;
    private final Resend resend;
    private final VerificationEmailService verificationEmailService;

    @Value("${resend.from-email}")
    private String fromEmail;

    public RegisterService(AccountRepository accountRepository, Resend resend,
            VerificationEmailService verificationEmailService) {
        this.accountRepository = accountRepository;
        this.resend = resend;
        this.verificationEmailService = verificationEmailService;
    }

    public String register(SignUpDTO dto) {
        Logger.i("Attempting to register new user: %s", dto.getEmail());
        UserRecord userRecord = null;

        try {
            // Create User in Firebase Auth
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(dto.getEmail())
                    .setPassword(dto.getPassword())
                    .setDisplayName(dto.getFirstName() + " " + dto.getLastName());

            userRecord = FirebaseAuth.getInstance().createUser(request);

            // Delete the password from the request body
            dto.setPassword(null);

            // Create Custom Token
            String customToken = FirebaseAuth.getInstance().createCustomToken(userRecord.getUid());

            // Create Account in Firestore
            Account account = new Account();
            AuthMeta authMeta = new AuthMeta();

            account.setUid(userRecord.getUid());
            account.setEmail(dto.getEmail());
            account.setFirstName(dto.getFirstName());
            account.setLastName(dto.getLastName());
            account.setUpdatedAt(Timestamp.now());
            account.setEnabled(true);
            authMeta.setCreatedAt(Timestamp.now());
            authMeta.setLastLoginAt(Timestamp.now());
            authMeta.setOnboarded(false);
            account.setAuthMeta(authMeta);

            accountRepository.createAccount(account);
            Logger.i("Registration successful for: %s", dto.getEmail());

            // Send welcome email
            CreateEmailOptions welcomeParams = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(userRecord.getEmail())
                .subject("Welcome to AllinMath!")
                .html("<p>Dear " + dto.getFirstName() + ",</p><p>Welcome to AllinMath! We're excited to have you on board.</p>")
                .build();
            resend.emails().send(welcomeParams);

            // Send verification email by calling the existing service
            verificationEmailService.send(userRecord.getUid());

            return customToken;

        } catch (FirebaseAuthException e) {
            Logger.i("Firebase Auth Error: %s", e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists or invalid data.");
        } catch (Exception e) {
            Logger.e("Registration failed: %s", e.getMessage());

            // ROLLBACK: Delete the Auth user if Firestore save fails
            if (userRecord != null) {
                try {
                    FirebaseAuth.getInstance().deleteUser(userRecord.getUid());
                    accountRepository.deleteAccount(userRecord.getUid());
                    Logger.w("Rolled back orphaned Auth user: %s", userRecord.getUid());
                } catch (FirebaseAuthException | ExecutionException | InterruptedException ex) {
                    Logger.e("Failed to rollback user: %s", ex.getMessage());
                }
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Registration failed. Please try again.");
        }
    }

    public void createMockStudents(String teacherUid) {
        java.util.List<Account> accountsToCreate = java.util.Collections.synchronizedList(new java.util.ArrayList<>());

        // Use parallel stream to speed up Auth creation
        java.util.stream.IntStream.rangeClosed(1, 100).parallel().forEach(i -> {
            String email = "studentTest" + i + "@test.com"; // Use consistent email
            // NOTE: parallel streams with static names might conflict if called multiple
            // times,
            // but for "mock accounts" this is fine.
            try {
                UserRecord userRecord;
                try {
                    userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
                    Logger.i("User already exists in Auth: " + email);
                } catch (FirebaseAuthException e) {
                    UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                            .setEmail(email)
                            .setPassword("123456")
                            .setDisplayName("Test Student " + i);
                    userRecord = FirebaseAuth.getInstance().createUser(request);
                }

                com.allinmath.backend.model.account.StudentProfile student = new com.allinmath.backend.model.account.StudentProfile();
                student.setUid(userRecord.getUid());
                student.setEmail(email);
                student.setFirstName("Test");
                student.setLastName("Student " + i);
                student.setRole(com.allinmath.backend.model.account.UserRole.STUDENT);
                student.setUpdatedAt(Timestamp.now());
                student.setEnabled(true);

                AuthMeta authMeta = new AuthMeta();
                authMeta.setCreatedAt(Timestamp.now());
                authMeta.setLastLoginAt(Timestamp.now());
                authMeta.setOnboarded(true);
                student.setAuthMeta(authMeta);

                student.setTeacherIDs(new java.util.ArrayList<>());
                student.addTeacherID(teacherUid);
                student.setGradeLevel(10);
                student.setSections(java.util.List.of("TYT", "AYT"));

                accountsToCreate.add(student);

            } catch (Exception e) {
                Logger.e("Failed to prepare mock student " + email + ": " + e.getMessage());
            }
        });

        if (!accountsToCreate.isEmpty()) {
            try {
                accountRepository.batchCreateAccounts(accountsToCreate);
                Logger.i("Successfully batch created " + accountsToCreate.size() + " mock students in Firestore.");
            } catch (ExecutionException | InterruptedException e) {
                Logger.e("Failed to batch create accounts: " + e.getMessage());
                // In a real scenario, we might want to rollback Auth users here
            }
        }
    }
}
