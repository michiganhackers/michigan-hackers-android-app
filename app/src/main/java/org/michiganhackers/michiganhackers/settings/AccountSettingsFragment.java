package org.michiganhackers.michiganhackers.settings;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.michiganhackers.michiganhackers.FirebaseAuthActivity;
import org.michiganhackers.michiganhackers.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class AccountSettingsFragment extends PreferenceFragmentCompat {
    private final String TAG = getClass().getCanonicalName();

    public AccountSettingsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() != null) {
            ((SettingsActivity) getActivity()).setToolbarTitle(R.string.account);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_account, rootKey);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey() != null && preference.getKey().equals(getString(R.string.pref_account_sign_out_key))) {
            showSignOutDialog();
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void showSignOutDialog() {
        if (getActivity() == null) {
            Log.e(TAG, "getActivity() == null in showSignOutDialog()");
            return;
        }
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setTitle(R.string.sign_out)
                .setMessage(R.string.sign_out_warning_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() != null) {
                            ((FirebaseAuthActivity) getActivity()).signOut();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

}

