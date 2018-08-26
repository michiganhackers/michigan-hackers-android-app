package org.michiganhackers.michiganhackers;

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

    private MutableLiveData<Map<String, Team>> teams;
    private Map<String, Team> teamsLocal;

    private MutableLiveData<Map<String, Member>> members;
    private Map<String, Member> membersLocal;

    private DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference().child("Teams");
    private DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child("Members");


    public DirectoryViewModel() {
        // teams can be ordered in treemap because their key is team name
        teamsLocal = new TreeMap<>();
        // members cannot be ordered in treemap because their key is user id (to allow for duplicate names)
        membersLocal = new HashMap<>();
        if (this.teams != null) {
            return;
        } else {
            teams = new MutableLiveData<>();
            members = new MutableLiveData<>();
            teamsRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Team team = dataSnapshot.getValue(Team.class);
                    // No nullptr exception b/c team cannot exist without a name
                    teamsLocal.put(team.getName(), team);
                    teams.setValue(teamsLocal);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Team team = dataSnapshot.getValue(Team.class);
                    teamsLocal.put(team.getName(), team);
                    teams.setValue(teamsLocal);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Team team = dataSnapshot.getValue(Team.class);
                    teamsLocal.remove(team.getName());
                    teams.setValue(teamsLocal);
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    // Not relevant because firebase data isn't ordered
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled for teamsRef addChildEventListener");
                }
            });

            membersRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Member member = dataSnapshot.getValue(Member.class);
                    // No nullptr exception b/c member cannot exist without a uid
                    membersLocal.put(member.getUid(), member);
                    members.setValue(membersLocal);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Member member = dataSnapshot.getValue(Member.class);
                    membersLocal.put(member.getUid(), member);
                    members.setValue(membersLocal);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Member member = dataSnapshot.getValue(Member.class);
                    membersLocal.remove(member.getUid());
                    members.setValue(membersLocal);
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    // Not relevant because firebase data isn't ordered
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled for membersRef addChildEventListener");
                }
            });

        }
    }

    LiveData<Map<String, Team>> getTeams() {
        return teams;
    }

    LiveData<Map<String, Member>> getMembers() {
        return members;
    }

    public void addMember(Member newMember, Uri filePath) {
        uploadProfilePhoto(newMember, filePath);
        DatabaseReference memberRef = membersRef.child(newMember.getUid());
        Member memberLocal = membersLocal.get(newMember.getUid());
        // If the member already exists and the name is different, change firebase auth display name
        if (memberLocal != null && !memberLocal.getName().equals(newMember.getName())) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newMember.getName()).build();
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
        memberRef.setValue(newMember);
    }

    public void addTeam(String teamName) {
        if (!teamsLocal.containsKey(teamName)) {
            Team team = new Team(teamName);
            teamsRef.child(teamName).setValue(team);
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

}