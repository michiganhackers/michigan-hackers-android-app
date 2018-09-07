package org.michiganhackers.michiganhackers;

import android.app.Activity;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;


public class ThemeHandler {
    private final Activity activity;
    static final String PREFS_NAME = "prefs";
    static final String PREF_THEME = "theme";
    private static String theme;


    public ThemeHandler(Activity activity) {
        this.activity = activity;
    }

    public void setTheme() {

        SharedPreferences preferences = activity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        theme = preferences.getString(PREF_THEME, "Light");
        switch(theme) {
            case "Light":
                activity.setTheme(R.style.BaseTheme);
                break;
            case "Dark":
                activity.setTheme(R.style.DarkTheme);
                break;
        }
    }
}
