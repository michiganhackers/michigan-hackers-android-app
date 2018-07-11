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

import java.util.ArrayList;

// Todo: it is a good practice when using fragments to check isAdded before getActivity() is called. This helps avoid a null pointer exception when the fragment is detached from the activity. OR getActivity() == null
public class DirectoryFragment extends Fragment {

    private DirectoryRecyclerViewAdapter directoryRecyclerViewAdapter;

    public DirectoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_directory, container, false);
        RecyclerView recyclerView = layout.findViewById(R.id.directory_recycler);
        // Improves recyclerView performance
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Initialize adapter
        directoryRecyclerViewAdapter = new DirectoryRecyclerViewAdapter();
        recyclerView.setAdapter(directoryRecyclerViewAdapter);
        return layout;
        
    }

}
