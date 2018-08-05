package org.michiganhackers.michiganhackers;

import android.arch.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.TreeMap;

public class DirectoryLiveData extends LiveData<Map<String, Team>> {
    private static DirectoryLiveData directoryLiveData;
    private Map<String, Team> teamsByName;
    private DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference().child("Teams");
    
    private DirectoryLiveData() {
        teamsByName = new TreeMap<>();
    }

    //TOdo add child event listener
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Team team = snapshot.getValue(Team.class);
                teamsByName.put(team.getName(), team);
            }
            setValue(teamsByName);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Todo
        }
    };

    @Override
    protected void onActive() {
        teamsRef.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onInactive() {
        teamsRef.removeEventListener(valueEventListener);
    }

    public static DirectoryLiveData get() {
        if (directoryLiveData == null) {
            directoryLiveData = new DirectoryLiveData();
        }
        return directoryLiveData;
    }
}