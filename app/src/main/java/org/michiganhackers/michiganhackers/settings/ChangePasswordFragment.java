package org.michiganhackers.michiganhackers.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.michiganhackers.michiganhackers.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

public class ChangePasswordFragment extends Fragment {
    private static final String actionBarTitle = "Change Password";

    public ChangePasswordFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_change_password, container, false);
        setActionBarTitle();
        return layout;
    }

    private void setActionBarTitle() {
        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle(actionBarTitle);
        }
    }

}
