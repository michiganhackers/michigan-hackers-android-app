package org.michiganhackers.michiganhackers.directory;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.michiganhackers.michiganhackers.R;

import java.util.Map;

// Todo: it is a good practice when using fra3gments to check isAdded before getActivity() is called. This helps avoid a null pointer exception when the fragment is detached from the activity. OR getActivity() == null
public class DirectoryFragment extends Fragment {
    private DirectoryViewModel directoryViewModel;

    public DirectoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        directoryViewModel = ViewModelProviders.of(getActivity()).get(DirectoryViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_directory, container, false);

        ExpandableListView expandableListView = layout.findViewById(R.id.list_view_directory);
        final DirectoryExpandableListAdapter directoryExpandableListAdapter = new DirectoryExpandableListAdapter(getContext());
        expandableListView.setAdapter(directoryExpandableListAdapter);

        final Observer<Map<String,Team>> teamsObserver = new Observer<Map<String,Team>>() {
            @Override
            public void onChanged(@Nullable final Map<String,Team> teams) {
                directoryExpandableListAdapter.setTeams(teams);
                directoryExpandableListAdapter.notifyDataSetChanged();
            }
        };
        directoryViewModel.getTeams().observe(this, teamsObserver);

        final Observer<Map<String,Member>> membersObserver = new Observer<Map<String,Member>>() {
            @Override
            public void onChanged(@Nullable final Map<String,Member> members) {
                directoryExpandableListAdapter.setMembers(members);
                directoryExpandableListAdapter.notifyDataSetChanged();
            }
        };
        directoryViewModel.getMembers().observe(this, membersObserver);

        return layout;
    }
}
