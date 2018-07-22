package org.michiganhackers.michiganhackers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.TreeMap;

public class UserDataRepo {
    private TreeMap<String, Team> teamsByName;
    private HashMap<DatabaseReference, ValueEventListener> valueEventListeners;
    private ExecuteOnDataChange executeOnDataChange;

    public UserDataRepo(TreeMap<String, Team> teamsByName_in) {
        this.teamsByName = teamsByName_in;
        setDirectoryListeners();
    }

    public UserDataRepo(TreeMap<String, Team> teamsByName_in, ExecuteOnDataChange function) {
        this.teamsByName = teamsByName_in;
        this.executeOnDataChange = function;
        setDirectoryListeners();
}

    private void setDirectoryListeners(){
        valueEventListeners = new HashMap<>();
        DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference().child("Teams");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    // Add team
                    String teamName = snapshot.getValue().toString();
                    teamsByName.put(teamName, new Team(teamName));

                    // Add members
                    // Todo: Currently somehow adds listener if reference to members does not yet exist. When members is created later, it does not have to re-add listener and gets updated member info. How?
                    DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child(teamName).child("Members");
                    if(!valueEventListeners.containsKey(membersRef)){
                        ValueEventListener valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    String memberName = snapshot.getValue().toString();
                                    String teamName = dataSnapshot.getRef().getParent().getKey();
                                    teamsByName.get(teamName).setMember(memberName, new Member(memberName, teamName));
                                }
                                if(executeOnDataChange != null){
                                    executeOnDataChange.executeOnDataChange();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //Todo
                            }
                        };
                        valueEventListeners.put(membersRef, valueEventListener);
                        membersRef.addValueEventListener(valueEventListener);
                    }
                }
                if(executeOnDataChange != null){
                    executeOnDataChange.executeOnDataChange();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Todo
            }
        };
        valueEventListeners.put(teamsRef, valueEventListener);
        teamsRef.addValueEventListener(valueEventListener);
    }
}


