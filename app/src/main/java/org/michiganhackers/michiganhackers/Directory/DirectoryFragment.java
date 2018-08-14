package org.michiganhackers.michiganhackers.Directory;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.michiganhackers.michiganhackers.R;

import java.util.Map;

// Todo: it is a good practice when using fragments to check isAdded before getActivity() is called. This helps avoid a null pointer exception when the fragment is detached from the activity. OR getActivity() == null
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_directory, container, false);

        ExpandableListView expandableListView = layout.findViewById(R.id.directory_expandableListView);
        final DirectoryExpandableListAdapter directoryExpandableListAdapter = new DirectoryExpandableListAdapter(getContext());
        expandableListView.setAdapter(directoryExpandableListAdapter);

        final Observer<Map<String,Team>> teamsByNameObserver = new Observer<Map<String,Team>>() {
            @Override
            public void onChanged(@Nullable final Map<String,Team> teamsByName) {
                directoryExpandableListAdapter.setTeamsByName(teamsByName);
                directoryExpandableListAdapter.notifyDataSetChanged();
            }
        };
        directoryViewModel.getTeamsByName().observe(this, teamsByNameObserver);

        return layout;
    }
}
