package org.michiganhackers.michiganhackers;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class AccountViewModel extends ViewModel {
    private static final String TAG = "AccountViewModel";

    private MutableLiveData<Member> member;

    private String uid;

    private DocumentReference memberRef;
    private StorageReference storageRef;

    public AccountViewModel(String uid) {
        this.uid = uid;

        memberRef = FirebaseFirestore.getInstance().collection("Members").document(uid);
        storageRef = FirebaseStorage.getInstance().getReference();

        if (this.member == null) {
            member = new MutableLiveData<>();
            memberRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    member.setValue(documentSnapshot.toObject(Member.class));
                                } else{
                                    member.setValue(new Member());
                                }
                            } else {
                                Log.e(TAG, "Failed to get member document: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void deleteProfilePhoto() {
        Member memberLocal = member.getValue();
        if (memberLocal != null && memberLocal.getPhotoUrl() != null) {
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(memberLocal.getPhotoUrl());
            photoRef.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed to delete member's old profile picture", e);
                }
            });
        }
    }

    public void removeMember(String uid) {
        deleteProfilePhoto();
        memberRef.delete();
    }

}