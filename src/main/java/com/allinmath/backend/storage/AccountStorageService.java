package com.allinmath.backend.storage;

import com.allinmath.backend.util.Logger;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AccountStorageService {

    private static final List<String> PFP_PREFIXES = Arrays.asList(
            "pfp/",
            "profile_pictures/",
            "profile/"
    );

    /**
     * Returns a short-lived signed URL for the user's profile picture if found.
     */
    public String getUserProfilePictureUrl(String uid) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket();
        if (bucket == null) {
            Logger.e("Firebase Storage bucket is not configured");
            throw new Exception("Storage bucket not configured");
        }

        Blob blob = findUserPfpBlob(bucket, uid);
        if (blob == null) {
            Logger.w("No profile picture found in storage for %s", uid);
            throw new Exception("Profile picture not found in storage");
        }

        Storage storage = bucket.getStorage();
        BlobInfo info = BlobInfo.newBuilder(blob.getBlobId()).build();
        URL signedUrl = storage.signUrl(
                info,
                15,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.withV4Signature()
        );
        return signedUrl.toString();
    }

    /**
     * Deletes the user's profile picture object from storage if it exists.
     */
    public void deleteUserProfilePicture(String uid) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket();
        if (bucket == null) {
            Logger.e("Firebase Storage bucket is not configured");
            throw new Exception("Storage bucket not configured");
        }

        Blob blob = findUserPfpBlob(bucket, uid);
        if (blob == null) {
            Logger.w("No profile picture found to delete for %s", uid);
            return; // idempotent
        }
        boolean deleted = blob.delete();
        if (!deleted) {
            Logger.w("Blob delete returned false for %s", blob.getName());
        }
    }

    private Blob findUserPfpBlob(Bucket bucket, String uid) {
        for (String prefixBase : PFP_PREFIXES) {
            String prefix = prefixBase + uid;
            // Try exact match first
            Blob exact = bucket.get(prefix);
            if (exact != null) return exact;

            // Try listing by prefix to account for file extensions (e.g., .jpg, .png)
            Page<Blob> page = bucket.list(Storage.BlobListOption.prefix(prefix));
            for (Blob b : page.iterateAll()) {
                // pick first match
                if (b != null) return b;
            }
        }
        return null;
    }
}
