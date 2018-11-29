package org.michiganhackers.michiganhackers;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.michiganhackers.michiganhackers.account.AccountActivity;
import org.michiganhackers.michiganhackers.directory.DirectoryViewModel;
import org.michiganhackers.michiganhackers.profile.ProfileActivity;

import static android.content.Context.MODE_PRIVATE;
import static org.michiganhackers.michiganhackers.ThemeHandler.PREFS_NAME;
import static org.michiganhackers.michiganhackers.ThemeHandler.PREF_THEME;

public class SettingsFragment extends Fragment {
    private DirectoryViewModel directoryViewModel;
    private static final String TAG = "SettingsFragment";
    private RadioGroup themeSelector;
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

        themeSelector = layout.findViewById(R.id.radio_group_select_theme);
        themeSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean checked = ((RadioButton) themeSelector.findViewById(checkedId)).isChecked();
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();

                switch(checkedId) {
                    case R.id.btn_light_theme:
                        if (checked) {
                            Log.i(TAG, "Light Theme Selected");
                            editor.putString(PREF_THEME, "Light");
                        }
                        break;
                    case R.id.btn_dark_theme:
                        if (checked) {
                            Log.i(TAG, "Dark Theme Selected");
                            editor.putString(PREF_THEME, "Dark");
                        }
                        break;
                }
                editor.apply();
                getActivity().recreate();
            }
        });

        final EditText etAddTeam = layout.findViewById(R.id.et_add_team);
        Button btnAddTeam = layout.findViewById(R.id.btn_add_team);
        btnAddTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etAddTeam.getText().toString().equals("")){
                    directoryViewModel.addTeam(etAddTeam.getText().toString());
                }
            }
        });

        return layout;
    }


}
