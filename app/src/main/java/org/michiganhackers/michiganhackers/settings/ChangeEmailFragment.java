package org.michiganhackers.michiganhackers.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.michiganhackers.michiganhackers.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

public class ChangeEmailFragment extends Fragment {
    public ChangeEmailFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_change_email, container, false);
        final String actionBarTitle = "Change Email";
        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle(actionBarTitle);
        }
        return layout;
    }

}

