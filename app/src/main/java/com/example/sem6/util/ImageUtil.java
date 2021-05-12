package com.example.sem6.util;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.Date;
import java.util.Random;

public class ImageUtil {
    public static void upload(
            InputStream is,
            OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener,
            OnFailureListener onFailureListener) {
        String filename = new Date().getTime() + new Random().nextInt(999999) + ".jpg";

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        StorageReference mountainsRef = storageRef.child(filename);

        UploadTask uploadTask = mountainsRef.putStream(is);

        uploadTask
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
}
