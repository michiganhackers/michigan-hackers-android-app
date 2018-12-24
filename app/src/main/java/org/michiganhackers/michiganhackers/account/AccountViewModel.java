package org.michiganhackers.michiganhackers.account;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.michiganhackers.michiganhackers.directory.Member;

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