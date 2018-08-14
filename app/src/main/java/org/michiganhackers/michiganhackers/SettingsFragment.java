package org.michiganhackers.michiganhackers;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import org.michiganhackers.michiganhackers.Directory.AccountActivity;
import org.michiganhackers.michiganhackers.Directory.LoginActivity;
import org.michiganhackers.michiganhackers.Directory.ProfileActivity;
import org.michiganhackers.michiganhackers.R;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
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
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() == null) {
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                else
                {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });

        Button editAccountButton = layout.findViewById(R.id.settings_accountButton);
        editAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() == null) {
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                else
                {
                    Intent intent = new Intent(getActivity(), AccountActivity.class);
                    getActivity().startActivity(intent);
                }

            }
        });

        return layout;
    }

}
