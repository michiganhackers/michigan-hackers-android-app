package org.michiganhackers.michiganhackers;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {
    private DirectoryViewModel directoryViewModel;

    public SettingsFragment() {
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
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_settings, container, false);

        Button editProfileButton = layout.findViewById(R.id.settings_editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });

        Button editAccountButton = layout.findViewById(R.id.settings_accountButton);
        editAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                getActivity().startActivity(intent);

            }
        });

        final EditText addTeamEditText = layout.findViewById(R.id.settings_addTeamEditText);
        Button addTeamButton = layout.findViewById(R.id.settings_addTeamButton);
        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directoryViewModel.addTeamToTeamsList(addTeamEditText.getText().toString());
            }
        });

        return layout;
    }

}
