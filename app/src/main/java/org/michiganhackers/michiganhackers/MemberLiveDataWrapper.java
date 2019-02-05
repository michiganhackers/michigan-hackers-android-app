package org.michiganhackers.michiganhackers;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.michiganhackers.michiganhackers.directory.Member;
import org.michiganhackers.michiganhackers.directory.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class MemberLiveDataWrapper {
    private final String TAG = getClass().getCanonicalName();

    // Note: This livedata is not updated in realtime
    private MutableLiveData<Member> member;

    private String uid;

    private DocumentReference memberRef;
    private StorageReference storageRef;

    public MemberLiveDataWrapper(String uid) {
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
                                if (documentSnapshot != null && documentSnapshot.exists()) {
                                    member.setValue(documentSnapshot.toObject(Member.class));
                                } else {
                                    member.setValue(new Member());
                                }
                            } else {
                                Log.e(TAG, "Failed to get member document: ", task.getException());
                            }
                        }
                    });
        }
    }


    public MutableLiveData<Member> getMember() {
        return member;
    }

    public void setMember(Member newMember, Uri filePath) {
        if (newMember.getUid().equals(uid)) {
            uploadProfilePhoto(filePath);
            Member memberLocal = member.getValue();

            // newMember.photoUrl is always null, so newMemberMap never has that field
            Map newMemberMap = Util.pojoToMap(newMember);

            // merge because we don't want to change the photoUrl field
            memberRef.set(newMemberMap, SetOptions.merge());
        } else {
            Log.e(TAG, "Attempted to add member whose uid did not match that of the ProfileViewModel uid");
        }
    }

    private void uploadProfilePhoto(Uri filePath) {
        if (filePath != null) {
            // delete old profile picture
            deleteProfilePhoto();
            // Upload picture and set member photoUri
            final StorageReference photoRef = storageRef.child("images/users/" + UUID.randomUUID().toString());
            photoRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            updateUserPhotoUrl(photoRef);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to add user profile image to storage", e);
                        }
                    });
        }
    }

    private void updateUserPhotoUrl(final StorageReference photoRef) {
        photoRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        memberRef.update("photoUrl", uri.toString())
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Failed to update member photoUrl", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to get download url from profile photo ref", e);
                    }
                });
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




