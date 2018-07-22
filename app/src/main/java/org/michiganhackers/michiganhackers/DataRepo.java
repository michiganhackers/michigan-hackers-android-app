package org.michiganhackers.michiganhackers;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class DataRepo {
    static Map<String, Team> teamsByName = new TreeMap<>();

    public static void setTeamsListener(final ExecuteOnDataChange executeOnDataChange){
        DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference().child("Teams");
        teamsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Team team = snapshot.getValue(Team.class);
                    teamsByName.put(team.getName(), team);
                    executeOnDataChange.executeOnDataChange();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Todo
            }
        });
    }
}


