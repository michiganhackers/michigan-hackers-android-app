package org.michiganhackers.michiganhackers;

import android.app.ProgressDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class DirectoryViewModel extends ViewModel {
    private static final String TAG = "DirectoryViewModel";

    private MutableLiveData<Map<String, Team>> teamsByName;
    private Map<String, Team> teamsByNameLocal;

    private MutableLiveData<List<String>> teamsList;
    private List<String> teamsListLocal;

    private DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference().child("Teams");
    private DatabaseReference teamsListRef = FirebaseDatabase.getInstance().getReference().child("TeamsList");

    public DirectoryViewModel() {
        teamsByNameLocal = new TreeMap<>();
        teamsListLocal = new ArrayList<>();
        if (this.teamsByName != null) {
            return;
        } else {
            teamsByName = new MutableLiveData<>();
            teamsList = new MutableLiveData<>();
            teamsRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Team team = dataSnapshot.getValue(Team.class);
                    // No nullptr exception b/c team cannot exist without a name
                    teamsByNameLocal.put(team.getName(), team);
                    teamsByName.setValue(teamsByNameLocal);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Team team = dataSnapshot.getValue(Team.class);
                    teamsByNameLocal.put(team.getName(), team);
                    teamsByName.setValue(teamsByNameLocal);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Team team = dataSnapshot.getValue(Team.class);
                    teamsByNameLocal.remove(team.getName());
                    teamsByName.setValue(teamsByNameLocal);
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //Todo
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled for teamsRef addChildEventListener");
                }
            });

            teamsListRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, String> teamsListMap = (HashMap<String,String>) dataSnapshot.getValue();
                    if(teamsListMap != null){
                        teamsListLocal = new ArrayList<>(teamsListMap.values());
                        teamsList.setValue(teamsListLocal);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled for teamsListsRef.addValueEventListener");
                }
            });

        }
    }

    LiveData<Map<String, Team>> getTeamsByName() {
        return teamsByName;
    }

    public void addMember(Member member, Uri filePath) {
        uploadProfilePhoto(member, filePath);
        if (teamsByNameLocal.containsKey(member.getTeam())) {
            DatabaseReference memberRef = teamsRef.child(member.getTeam()).child("members").child(member.getUid());
            Member memberLocal = teamsByNameLocal.get(member.getTeam()).getMember(member.getUid());
            // If the member already exists and the name is different, change firebase auth display name
            if (memberLocal != null && !memberLocal.getName().equals(member.getName())) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(member.getName()).build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e(TAG, "Failed to update auth display name");
                                    }
                                }
                            });
                }
            }
            memberRef.setValue(member, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        //todo: This is if data could not be saved
                    } else {

                    }
                }
            });
        } else  if(teamsListLocal.contains(member.getTeam())){
            Team team = new Team(member.getTeam());
            team.setMember(member);
            teamsRef.child(member.getTeam()).setValue(team, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        //todo: This is if data could not be saved
                    } else {

                    }
                }
            });
        } else{
            Log.e(TAG,"Attempted to add member to non-existent team");
        }
    }

    public Member getMember(String uid) {
        for (Map.Entry<String, Team> team : teamsByNameLocal.entrySet()) {
            Member member = team.getValue().getMember(uid);
            if (member != null) {
                return member;
            }
        }
        return null;
    }

    public void removeMember(String uid) {
        for (Map.Entry<String, Team> team : teamsByNameLocal.entrySet()) {
            Member member = team.getValue().getMember(uid);
            if (member != null) {
                DatabaseReference memberRef = teamsRef.child(member.getTeam()).child("members").child(uid);
                memberRef.removeValue();
            }
        }
    }

    private void uploadProfilePhoto(final Member member, Uri filePath) {
        if (filePath != null) {
            // If the member already exists, delete current profile picture
            if (teamsByNameLocal.containsKey(member.getTeam())) {
                Member memberLocal = teamsByNameLocal.get(member.getTeam()).getMember(member.getUid());
                if (memberLocal != null && memberLocal.getPhotoUrl() != null) {
                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(memberLocal.getPhotoUrl());
                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // todo
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // todo
                        }
                    });
                }
            }
            // Upload picture and set user photoUri
            final StorageReference photoRef = FirebaseStorage.getInstance().getReference().child("images/users/" + UUID.randomUUID().toString());
            photoRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // todo: add logic for listening to this to finish from profileActivity
                            // Add url of uploaded image to user profile
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        DatabaseReference memberPhotoUrlRef = teamsRef.child(member.getTeam()).child("members").child(member.getUid()).child("photoUrl");
                                        memberPhotoUrlRef.setValue(uri.toString());
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful()) {
                                                            Log.e(TAG, "Failed to update auth photoUri");
                                                        }
                                                    }
                                                });
                                    }
                                });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //todo
                        }
                    });
        }
    }

    public MutableLiveData<List<String>> getTeamsList() {
        return teamsList;
    }

    public void addTeamToTeamsList(String team){
        if(!teamsListLocal.contains(team)){
            teamsListRef.push().setValue(team);
        }
    }

}