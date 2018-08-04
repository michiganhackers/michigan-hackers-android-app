package org.michiganhackers.michiganhackers;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.TreeMap;

public class DirectoryViewModel extends ViewModel{
    private MutableLiveData<Map<String, Team>> teamsByName;
    private Map<String, Team> teamsByNameLocal; //TOdo add child event listener
    public DirectoryViewModel(){
        teamsByNameLocal = new TreeMap<>();
        if(this.teamsByName != null){
            return;
        }
        else{
            teamsByName = new MutableLiveData<>();
            DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference().child("Teams");
            teamsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String,Team> teamsByName2 = new TreeMap<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Team team = snapshot.getValue(Team.class);
                        teamsByName2.put(team.getName(), team);
                    }
                    teamsByName.setValue(teamsByName2);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Todo
                }
            });

        }
    }
    MutableLiveData<Map<String, Team>> getTeamsByNameMLD(){
        return teamsByName;
    }

    public Map<String, Team> getTeamsByName() {
        return teamsByNameLocal;
    }
}