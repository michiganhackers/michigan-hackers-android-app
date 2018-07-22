package org.michiganhackers.michiganhackers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Comparator;
import java.util.TreeMap;

public class DataRepo {
    static TreeMap<String, Team> teamsByName = new TreeMap<>();

    public static void setDirectoryListeners(final ExecuteOnDataChange executeOnDataChange){
        DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference().child("Teams");
        teamsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    // Add team
                    Team team = dataSnapshot.getValue(Team.class);
                    teamsByName.put(team.getName(), team);

                    // Add members
                    DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child(team.getKey()).child("Members");
                        membersRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                Member member = dataSnapshot.getValue(Member.class);
                                teamsByName.get(member.getTeam()).setMember(member.getName(), member);
                                if(executeOnDataChange != null){
                                    executeOnDataChange.executeOnDataChange();
                                }
                            }
                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //Todo
                            }
                        });

                if(executeOnDataChange != null){
                    executeOnDataChange.executeOnDataChange();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Todo
            }
        });
    }
}


