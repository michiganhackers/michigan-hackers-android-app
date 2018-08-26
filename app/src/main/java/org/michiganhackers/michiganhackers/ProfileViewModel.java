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

public class ProfileViewModel extends ViewModel {
    private static final String TAG = "ProfileViewModel";

    // Note: These livedata are not updated in realtime
    private MutableLiveData<Map<String, Team>> teams;
    private MutableLiveData<Member> member;

    private LiveData<List<String>> teamNames;

    private String uid;

    private CollectionReference teamsRef;
    private DocumentReference memberRef;
    private StorageReference storageRef;


    public ProfileViewModel(String uid) {
        this.uid = uid;

        teamsRef = FirebaseFirestore.getInstance().collection("Teams");
        memberRef = FirebaseFirestore.getInstance().collection("Members").document(uid);
        storageRef = FirebaseStorage.getInstance().getReference();

        if (this.teams == null){
            teams = new MutableLiveData<>();
            member = new MutableLiveData<>();
            teamNames = Transformations.map(teams, new Function<Map<String, Team>, List<String>>() {
                @Override
                public List<String> apply(Map<String, Team> input) {
                    return new ArrayList<>(input.keySet());
                }
            });

            // Note: The livedata are not updated in realtime
            teamsRef.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            Map<String, Team> teamsLocal = new TreeMap<>();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                Team team = documentSnapshot.toObject(Team.class);
                                teamsLocal.put(team.getName(), team);
                            }
                            teams.setValue(teamsLocal);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to get teams documents: ", e);
                        }
                    });

            memberRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot.exists()){
                                    member.setValue(documentSnapshot.toObject(Member.class));

                                }
                                else{
                                    Log.e(TAG, "Member document does not exist:  ", task.getException());
                                }
                            }
                            else{
                                Log.e(TAG, "Failed to get member document: ", task.getException());
                            }
                        }
                    });

        }
    }

    public LiveData<List<String>> getTeamNames() {
        return teamNames;
    }

    public void setMember(Member newMember, Uri filePath) {
        if (newMember.getUid().equals(uid)) {
            uploadProfilePhoto(filePath);
            Member memberLocal = member.getValue();
            // If the member already exists and the name is different, change firebase auth display name
            if (memberLocal != null && !memberLocal.getName().equals(newMember.getName())) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newMember.getName()).build();
                    user.updateProfile(profileUpdates)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Failed to update auth display name", e);
                                }
                            });
                }
            }

            memberRef.set(newMember);
        } else {
            Log.e(TAG, "Attempted to add member whose uid did not match that of the ProfileViewModel uid");
        }
    }


    private void uploadProfilePhoto(Uri filePath) {
        if (filePath != null) {
            // If the member already exists, delete current profile picture
            Member memberLocal = this.member.getValue();
            if (memberLocal != null && memberLocal.getPhotoUrl() != null) {
                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(memberLocal.getPhotoUrl());
                photoRef.delete().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to delete member's old profile picture", e);
                    }
                });
            }
            // Upload picture and set member photoUri
            final StorageReference photoRef = storageRef.child("images/users/" + UUID.randomUUID().toString());
            photoRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Add url of uploaded image to user profile
                            photoRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            if (user != null) {
                                                memberRef.update("photoUrl", uri.toString())
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.e(TAG,"Failed to update member photoUrl",e);
                                                            }
                                                        });
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                                                user.updateProfile(profileUpdates)
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.e(TAG, "Failed to update user photoUri", e);
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Failed to get download url from profile photo ref", e);
                                        }
                                    });

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

}