package com.allinmath.backend.service.account;

import com.allinmath.backend.dto.account.CompleteOnboardingDTO;
import com.allinmath.backend.model.account.*;
import com.allinmath.backend.repository.account.AccountRepository;
import com.allinmath.backend.util.Logger;
import com.google.cloud.Timestamp;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class CompleteOnboardingService {

    private final AccountRepository accountRepository;

    public CompleteOnboardingService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void complete(String uid, CompleteOnboardingDTO dto) {
        try {
            Logger.i("Completing onboarding for user: %s", uid);
            Account account = accountRepository.getAccount(uid);
            if (account == null) {
                throw new RuntimeException("Account not found");
            }

            UserRole role = dto.getRole();
            if (role == null) {
                 throw new IllegalArgumentException("Role is required");
            }

            Account updatedAccount;

            if (role == UserRole.STUDENT) {
                 StudentProfile student = new StudentProfile();
                 copyAccountFields(account, student);
                 student.setRole(UserRole.STUDENT);
                 updatedAccount = student;
            } else if (role == UserRole.TEACHER) {
                 TeacherProfile teacher = new TeacherProfile();
                 copyAccountFields(account, teacher);
                 teacher.setTytCourses(dto.getTytCourses() != null ? dto.getTytCourses() : new java.util.ArrayList<>());
                 teacher.setAytCourses(dto.getAytCourses() != null ? dto.getAytCourses() : new java.util.ArrayList<>());
                 teacher.setRole(UserRole.TEACHER);
                 updatedAccount = teacher;
            } else {
                 updatedAccount = account;
                 updatedAccount.setRole(role);
            }

            // Common updates
            updatedAccount.setAge(dto.getAge());
            updatedAccount.setSections(dto.getSections());

            AuthMeta meta = updatedAccount.getAuthMeta();
            if (meta == null) {
                meta = new AuthMeta();
                meta.setCreatedAt(Timestamp.now()); 
            }
            meta.setOnboarded(true);
            updatedAccount.setAuthMeta(meta);
            updatedAccount.setUpdatedAt(Timestamp.now());

            // Use updateAccount which uses .set() to overwrite/save the new polymorphic object
            accountRepository.updateAccount(updatedAccount);
            Logger.i("Onboarding completed for user: %s as %s", uid, role);

        } catch (ExecutionException | InterruptedException e) {
            Logger.e("Error completing onboarding for %s: %s", uid, e.getMessage());
            throw new RuntimeException("Failed to complete onboarding");
        }
    }

    private void copyAccountFields(Account source, Account target) {
        target.setUid(source.getUid());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setEmail(source.getEmail());
        target.setBio(source.getBio());
        target.setProfileImageUrl(source.getProfileImageUrl());
        target.setSections(source.getSections());
        // updatedAt is updated in main method
        target.setEnabled(source.isEnabled());
        target.setAuthMeta(source.getAuthMeta());
    }
}
