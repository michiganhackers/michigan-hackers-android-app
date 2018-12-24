package org.michiganhackers.michiganhackers;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatDelegate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import org.michiganhackers.michiganhackers.account.AccountActivity;
import org.michiganhackers.michiganhackers.directory.DirectoryViewModel;
import org.michiganhackers.michiganhackers.profile.ProfileActivity;

import static org.michiganhackers.michiganhackers.MainActivity.THEME;

public class SettingsFragment extends Fragment {
    private DirectoryViewModel directoryViewModel;
    private static final String TAG = "SettingsFragment";

    public SettingsFragment() {
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
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_settings, container, false);

        Button btnEditProfile = layout.findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });

        Button editAccountButton = layout.findViewById(R.id.btn_goto_acct);
        editAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                getActivity().startActivity(intent);

            }
        });

        Button btnSendFeedback = layout.findViewById(R.id.btn_send_feedback);
        btnSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/forms/DYHVcfx12jJJgrAu1"));
                startActivity(browserIntent);
            }
        });

        Switch switchThemeSelector = layout.findViewById(R.id.switch_theme_selector);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int currentTheme = sharedPreferences.getInt(THEME, AppCompatDelegate.MODE_NIGHT_NO);
        if (currentTheme == AppCompatDelegate.MODE_NIGHT_NO) {
            switchThemeSelector.setChecked(false);

        } else {
            switchThemeSelector.setChecked(true);

        }
        switchThemeSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                int currentTheme = sharedPreferences.getInt(THEME, AppCompatDelegate.MODE_NIGHT_NO);
                if (isChecked && currentTheme != AppCompatDelegate.MODE_NIGHT_YES) {
                    sharedPreferences.edit().putInt(THEME, AppCompatDelegate.MODE_NIGHT_YES).apply();
                    getActivity().recreate();

                } else if (!isChecked && currentTheme != AppCompatDelegate.MODE_NIGHT_NO) {
                    sharedPreferences.edit().putInt(THEME, AppCompatDelegate.MODE_NIGHT_NO).apply();
                    getActivity().recreate();
                }

            }
        });


        final EditText etAddTeam = layout.findViewById(R.id.et_add_team);
        Button btnAddTeam = layout.findViewById(R.id.btn_add_team);
        btnAddTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etAddTeam.getText().toString().equals("")) {
                    directoryViewModel.addTeam(etAddTeam.getText().toString());
                }
            }
        });

        return layout;
    }


}
