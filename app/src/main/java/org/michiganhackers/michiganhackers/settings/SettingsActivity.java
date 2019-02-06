package org.michiganhackers.michiganhackers.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;

import org.michiganhackers.michiganhackers.FirebaseAuthActivity;
import org.michiganhackers.michiganhackers.R;
import org.michiganhackers.michiganhackers.eventList.CalendarEvent;

public class SettingsActivity extends FirebaseAuthActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    public static final String START_FRAGMENT = "start_fragment";

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupToolbar();
        initializeStartFragment();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initializeStartFragment() {
        if (getIntent() != null && getIntent().getStringExtra(START_FRAGMENT) != null) {
            final Bundle args = new Bundle();
            final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                    getClassLoader(), getIntent().getStringExtra(START_FRAGMENT), args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment(),
                args);
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
        return true;
    }

    public void setToolbarTitle(int titleStringResource) {
        toolbar.setTitle(titleStringResource);
    }

}
