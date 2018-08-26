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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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

    // teams can be ordered in treemap because their key is team name
    // members cannot be ordered in treemap because their key is user id (to allow for duplicate names)
    private MutableLiveData<Map<String, Team>> teams;
    private MutableLiveData<Map<String, Member>> members;

    CollectionReference teamsRef, membersRef;


    public DirectoryViewModel() {
        teamsRef = FirebaseFirestore.getInstance().collection("Teams");
        membersRef = FirebaseFirestore.getInstance().collection("Members");

        if (this.teams == null){
            teams = new MutableLiveData<>();
            members = new MutableLiveData<>();

            teams = new MutableLiveData<>();
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
                            Log.e(TAG, "Failed to lead teams documents: ", e);
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

    public Member getMember(String uid) {
        for (Map.Entry<String, Team> team : teamsByNameLocal.entrySet()) {
            Member member = team.getValue().getMember(uid);
            if (member != null) {
                return member;
            }
        }
        return null;
    }

}