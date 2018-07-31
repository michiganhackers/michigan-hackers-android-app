package org.michiganhackers.michiganhackers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

// Todo: it is a good practice when using fragments to check isAdded before getActivity() is called. This helps avoid a null pointer exception when the fragment is detached from the activity. OR getActivity() == null
public class DirectoryFragment extends Fragment {

    public DirectoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_directory, container, false);

        ExpandableListView expandableListView = layout.findViewById(R.id.directory_expandableListView);
        final DirectoryExpandableListAdapter directoryExpandableListAdapter = new DirectoryExpandableListAdapter(getContext());
        DirectoryRepository.setTeamsListener(new ExecuteOnDataChange() {
            @Override
            public void executeOnDataChange() {
                directoryExpandableListAdapter.notifyDataSetChanged();
            }
        });
        expandableListView.setAdapter(directoryExpandableListAdapter);

        Button editProfileButton = layout.findViewById(R.id.directroy_editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return layout;
    }
}
