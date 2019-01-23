package org.michiganhackers.michiganhackers.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.michiganhackers.michiganhackers.R;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;

public class MainSettingsFragment extends PreferenceFragmentCompat {

    // Used to store a strong reference to the listener so it doesn't get garbage collected as long
    // as the fragment is alive
    private customSharedPreferenceChangeListener onSharedPreferenceChangeListener;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_main, rootKey);

        Context currentContext = getContext();
        if(currentContext != null){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener = new customSharedPreferenceChangeListener());
        }
    }

    class customSharedPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Activity currentActivity = getActivity();
            if(currentActivity != null){
                if (key.equals(getString(R.string.pref_is_dark_theme_set_key))) {
                    boolean isDarkThemeSet = sharedPreferences.getBoolean(getString(R.string.pref_is_dark_theme_set_key), false);
                    if (isDarkThemeSet) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
                currentActivity.recreate();
            }
        }
    };
}

