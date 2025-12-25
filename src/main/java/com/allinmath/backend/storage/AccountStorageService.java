package com.allinmath.backend.storage;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
public class AccountStorageService {

    public String getUserProfilePictureUrl(String uid) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket();
        if (bucket == null) throw new Exception("Storage bucket not configured");

        Blob blob = findUserPfpBlob(bucket, uid);
        if (blob == null) {
            return null; // Return null instead of throwing, simpler for frontend to handle "no image"
        }

        // Generate Signed URL valid for 1 hour
        URL signedUrl = bucket.getStorage().signUrl(
                BlobInfo.newBuilder(blob.getBlobId()).build(),
                1, TimeUnit.HOURS,
                Storage.SignUrlOption.withV4Signature()
        );
        return signedUrl.toString();
    }

    public void deleteUserProfilePicture(String uid) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket();
        if (bucket == null) throw new Exception("Storage bucket not configured");

        Blob blob = findUserPfpBlob(bucket, uid);
        if (blob != null) {
            blob.delete();
        }
    }

    private Blob findUserPfpBlob(Bucket bucket, String uid) {
        String prefix = "profile_pictures/" + uid;

        // Try exact match (fastest)
        Blob exact = bucket.get(prefix);
        if (exact != null) return exact;

        // Try prefix match (slower, for extensions like .jpg)
        Page<Blob> page = bucket.list(Storage.BlobListOption.prefix(prefix));
        for (Blob b : page.iterateAll()) {
            if (b != null) return b;
        }

        return null;
    }
}