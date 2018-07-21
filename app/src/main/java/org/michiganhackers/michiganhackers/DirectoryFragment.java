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
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

// Todo: it is a good practice when using fragments to check isAdded before getActivity() is called. This helps avoid a null pointer exception when the fragment is detached from the activity. OR getActivity() == null
public class DirectoryFragment extends Fragment {

    private TreeMap<String, Team> teamsByName;
    private DirectoryExpandableListAdapter directoryExpandableListAdapter;
    private HashMap<DatabaseReference, ValueEventListener> valueEventListeners;
    public DirectoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_directory, container, false);
        ExpandableListView expandableListView = layout.findViewById(R.id.directory_expandableListView);
        if(teamsByName == null){
            teamsByName = new TreeMap<>();
        }
        setDirectoryListeners();
        directoryExpandableListAdapter = new DirectoryExpandableListAdapter(getContext(),teamsByName);
        expandableListView.setAdapter(directoryExpandableListAdapter);
        return layout;
    }

    // Note: App data is not updated in real time when database data is deleted to not interfere with user
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
                                directoryExpandableListAdapter.notifyDataSetChanged();
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
                // Update adapter
                directoryExpandableListAdapter.notifyDataSetChanged();
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
