package org.michiganhackers.michiganhackers.directory;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DirectoryViewModel extends ViewModel {
    private static final String TAG = "DirectoryViewModel";

    // teams can be ordered in treemap because their key is team name
    // members cannot be ordered in treemap because their key is user id (to allow for duplicate names)
    private MutableLiveData<Map<String, Team>> teams;
    private MutableLiveData<Map<String, Member>> members;

    private CollectionReference teamsRef, membersRef;


    public DirectoryViewModel() {
        teamsRef = FirebaseFirestore.getInstance().collection("Teams");
        membersRef = FirebaseFirestore.getInstance().collection("Members");

        if (this.teams == null) {
            teams = new MutableLiveData<>();
            members = new MutableLiveData<>();

            teams = new MutableLiveData<>();
            teamsRef
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.e(TAG, "teamsRef snapshot listen error", e);
                                return;
                            }

                            if (snapshots != null) {
                                if(snapshots.getDocumentChanges().size() == 0) {
                                    Map<String, Team> teamsLocal = new TreeMap<>();
                                    teams.setValue(teamsLocal);
                                }
                                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                    Team team = dc.getDocument().toObject(Team.class);
                                    Map<String, Team> teamsLocal = teams.getValue() == null ? new TreeMap<String, Team>() : teams.getValue();
                                    switch (dc.getType()) {
                                        case ADDED:
                                        case MODIFIED:
                                            teamsLocal.put(team.getName(), team);
                                            teams.setValue(teamsLocal);
                                            break;
                                        case REMOVED:
                                            teamsLocal.remove(team.getName());
                                            teams.setValue(teamsLocal);
                                            break;
                                    }
                                }
                            }

                        }
                    });


            membersRef
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.e(TAG, "membersRef snapshot listen error", e);
                                return;
                            }

                            if (snapshots != null) {
                                if(snapshots.getDocumentChanges().size() == 0) {
                                    Map<String, Member> membersLocal = new HashMap<>();
                                    members.setValue(membersLocal);
                                }
                                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                    Member member = dc.getDocument().toObject(Member.class);
                                    Map<String, Member> membersLocal = members.getValue() == null ? new HashMap<String, Member>() : members.getValue();
                                    switch (dc.getType()) {
                                        case ADDED:
                                        case MODIFIED:
                                            membersLocal.put(member.getUid(), member);
                                            members.setValue(membersLocal);
                                            break;
                                        case REMOVED:
                                            membersLocal.remove(member.getUid());
                                            members.setValue(membersLocal);
                                            break;
                                    }
                                }
                            }

                        }
                    });

        }
    }

    public LiveData<Map<String, Team>> getTeams() {
        return teams;
    }

    public LiveData<Map<String, Member>> getMembers() {
        return members;
    }

    public void addTeam(String teamName){
        if(teams.getValue() != null && !teams.getValue().containsKey(teamName)){
            teamsRef.document(teamName).set(new Team(teamName));
        }
    }

}