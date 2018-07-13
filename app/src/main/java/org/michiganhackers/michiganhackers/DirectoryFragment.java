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

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Todo: it is a good practice when using fragments to check isAdded before getActivity() is called. This helps avoid a null pointer exception when the fragment is detached from the activity. OR getActivity() == null
public class DirectoryFragment extends Fragment {

    private DirectoryExpandableListAdapter directoryExpandableListAdapter;
    private List<Team> teams;
    HashMap<String, List<Member>> members;

    public DirectoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_directory, container, false);
        ExpandableListView expandableListView = layout.findViewById(R.id.directory_expandableListView);
        getDirectoryData();
        directoryExpandableListAdapter = new DirectoryExpandableListAdapter(getContext(),teams,members);
        expandableListView.setAdapter(directoryExpandableListAdapter);
        return layout;
        
    }

    private void getDirectoryData(){
    }

}
