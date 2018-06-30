package org.michiganhackers.michiganhackersapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mainNav;
    private FrameLayout mainFrame;

    private ListFragment listFragment;
    private CalendarFragment calendarFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainNav = findViewById(R.id.main_nav);
        mainFrame = findViewById(R.id.main_frame);

        listFragment = new ListFragment();
        calendarFragment = new CalendarFragment();
        settingsFragment = new SettingsFragment();

        // Set initial fragment to listFragment
        // Todo: should the initial fragment use add instead of replace?
        replaceFragment(R.id.main_frame, listFragment);

        // Replace current fragment with one corresponding to which navigation item is selected
        mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_list:
                        replaceFragment(R.id.main_frame, listFragment);
                        return true;
                    case R.id.nav_calendar:
                        replaceFragment(R.id.main_frame, calendarFragment);
                        return true;
                    case R.id.nav_settings:
                        replaceFragment(R.id.main_frame, settingsFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    // Replace fragment in specified container
    private void replaceFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.commit();
    }
}
