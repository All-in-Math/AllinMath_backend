package com.allinmath.backend.model.account;

import com.google.cloud.Timestamp;

public class AuthMeta {
    private Timestamp createdAt;
    private Timestamp lastLoginAt;
    private Timestamp passwordUpdatedAt;
    private Timestamp emailVerifiedAt;
    private Timestamp disabledAt;
    private Timestamp deletedAt;
    private Timestamp bannedAt;
    private Timestamp unbannedAt;
    private Timestamp lastPasswordResetAt;
    private Timestamp lastEmailVerificationSentAt;
    private boolean onboarded;

    public AuthMeta() {}

    public Timestamp getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Timestamp lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getPasswordUpdatedAt() {
        return passwordUpdatedAt;
    }

    public void setPasswordUpdatedAt(Timestamp passwordUpdatedAt) {
        this.passwordUpdatedAt = passwordUpdatedAt;
    }

    public Timestamp getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(Timestamp emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public Timestamp getDisabledAt() {
        return disabledAt;
    }

    public void setDisabledAt(Timestamp disabledAt) {
        this.disabledAt = disabledAt;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Timestamp getBannedAt() {
        return bannedAt;
    }

    public void setBannedAt(Timestamp bannedAt) {
        this.bannedAt = bannedAt;
    }

    public Timestamp getUnbannedAt() {
        return unbannedAt;
    }

    public void setUnbannedAt(Timestamp unbannedAt) {
        this.unbannedAt = unbannedAt;
    }

    public Timestamp getLastPasswordResetAt() {
        return lastPasswordResetAt;
    }

    public void setLastPasswordResetAt(Timestamp lastPasswordResetAt) {
        this.lastPasswordResetAt = lastPasswordResetAt;
    }

    public Timestamp getLastEmailVerificationSentAt() {
        return lastEmailVerificationSentAt;
    }

    public void setLastEmailVerificationSentAt(Timestamp lastEmailVerificationSentAt) {
        this.lastEmailVerificationSentAt = lastEmailVerificationSentAt;
    }

    public boolean isOnboarded() {
        return onboarded;
    }

    public void setOnboarded(boolean onboarded) {
        this.onboarded = onboarded;
    }

    public boolean isEmailVerified() {
        return emailVerifiedAt != null;
    }

    public boolean isDisabled() {
        return disabledAt != null;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isBanned() {
        return bannedAt != null;
    }

    public boolean isUnbanned() {
        return unbannedAt != null;
    }

    public boolean isPasswordExpired() {
        return passwordUpdatedAt != null && passwordUpdatedAt.toDate().before(createdAt.toDate());
    }

    public boolean isEmailVerificationExpired() {
        return lastEmailVerificationSentAt != null && lastEmailVerificationSentAt.toDate().before(createdAt.toDate());
    }

    public boolean isPasswordResetExpired() {
        return lastPasswordResetAt != null && lastPasswordResetAt.toDate().before(createdAt.toDate());
    }

    public boolean isAccountExpired() {
        return disabledAt != null || deletedAt != null || bannedAt != null;
    }

    public boolean isAccountLocked() {
        return isBanned();
    }

    public boolean isAccountEnabled() {
        return !isAccountExpired() && !isAccountLocked();
    }

    public boolean isCredentialsExpired() {
        return isPasswordExpired() || isEmailVerificationExpired() || isPasswordResetExpired();
    }

    public String toString() {
        return "AuthMeta [createdAt=" + createdAt + ", lastLoginAt=" + lastLoginAt + ", passwordUpdatedAt=" + passwordUpdatedAt + ", emailVerifiedAt=" + emailVerifiedAt + ", disabledAt=" + disabledAt + ", deletedAt=" + deletedAt + ", bannedAt=" + bannedAt + ", unbannedAt=" + unbannedAt + ", lastPasswordResetAt=" + lastPasswordResetAt + ", lastEmailVerificationSentAt=" + lastEmailVerificationSentAt + "]";
    }
}
