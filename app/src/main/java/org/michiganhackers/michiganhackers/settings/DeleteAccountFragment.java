package org.michiganhackers.michiganhackers.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.michiganhackers.michiganhackers.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

public class DeleteAccountFragment extends Fragment {
    private static final String actionBarTitle = "Delete Account";

    public DeleteAccountFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_delete_account, container, false);

        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle(actionBarTitle);
        }

        return layout;
    }

}

