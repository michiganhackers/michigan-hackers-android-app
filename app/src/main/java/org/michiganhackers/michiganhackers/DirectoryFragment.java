package org.michiganhackers.michiganhackers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;

// Todo: it is a good practice when using fragments to check isAdded before getActivity() is called. This helps avoid a null pointer exception when the fragment is detached from the activity. OR getActivity() == null
public class DirectoryFragment extends Fragment {

    private DirectoryExpandableListAdapter directoryExpandableListAdapter;
    private SortedSet<Team> teams;
    HashMap<String, SortedSet<Member>> membersByTeam;

    public DirectoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_directory, container, false);
        ExpandableListView expandableListView = layout.findViewById(R.id.directory_expandableListView);
        if(teams == null){
            teams = new ArrayList<>();
        }
        if(membersByTeam == null){
            membersByTeam = new HashMap<>();
        }
        getDirectoryData();
        directoryExpandableListAdapter = new DirectoryExpandableListAdapter(getContext(),teams,membersByTeam);
        expandableListView.setAdapter(directoryExpandableListAdapter);
        return layout;
        
    }

    private void getDirectoryData(){
        DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference().child("Teams");
        teamsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Todo: Prevent duplicates
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String teamName = snapshot.getValue(String.class);
                    teams.add(new Team(teamName));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Todo
            }
        });
        for(final Team team : teams){
            DatabaseReference memberRef = FirebaseDatabase.getInstance().getReference().child(team.getName());
            memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SortedSet<Member> members = new
                    // Todo: Prevent duplicates
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String memberName = snapshot.getValue(String.class);
                        members.add(new Member(memberName, team.getName()));
                    }
                    membersByTeam.put(team.getName(), members);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Todo
                }
            });
        }
    }
}
