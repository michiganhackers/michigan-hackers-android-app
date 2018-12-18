package org.michiganhackers.michiganhackers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;

import org.michiganhackers.michiganhackers.directory.DirectoryFragment;
import org.michiganhackers.michiganhackers.eventList.CalendarAPI;
import org.michiganhackers.michiganhackers.eventList.ListFragment;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.michiganhackers.michiganhackers.eventList.CalendarAPI.PREF_ACCOUNT_NAME;
import static org.michiganhackers.michiganhackers.eventList.CalendarAPI.REQUEST_ACCOUNT_PICKER;
import static org.michiganhackers.michiganhackers.eventList.CalendarAPI.REQUEST_AUTHORIZATION;
import static org.michiganhackers.michiganhackers.eventList.CalendarAPI.REQUEST_GOOGLE_PLAY_SERVICES;
import static org.michiganhackers.michiganhackers.eventList.CalendarAPI.SCOPES;

public class MainActivity extends AppCompatActivity{

    private BottomNavigationView bottomNav;
    private android.view.MenuItem prevMenuItem;

    private ListFragment listFragment;
    private SettingsFragment settingsFragment;
    private DirectoryFragment directoryFragment;

    private CalendarAPI calAPI;
    private NotificationHandler notification;
    private ViewPager viewPager;
    private static final String TAG = "MainActivity";
    static final String THEME = "THEME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = sharedPreferences.getInt(THEME, AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listFragment = new ListFragment();
        settingsFragment = new SettingsFragment();
        directoryFragment = new DirectoryFragment();

        calAPI = new CalendarAPI(this, this, listFragment);
        notification = new NotificationHandler(this, this);
        notification.createNotificationChannel();

        if(savedInstanceState == null) {
            calAPI.mCredential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
            calAPI.getResultsFromApi();
        }

        viewPager = findViewById(R.id.view_pager);
        FragmentPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(mainPagerAdapter);

        bottomNav = findViewById(R.id.bottom_nav);


        // Replace current fragment with one corresponding to which navigation item is selected
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_list:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.menu_directory:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.menu_settings:
                        viewPager.setCurrentItem(2);
                        return true;
                    default:
                        return false;
                }
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNav.getMenu().getItem(0).setChecked(false);
                }
                bottomNav.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNav.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state){

            }
        });


    }

    // Todo: Bundles should only hold a small amount of data. Change to viewmodel
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public ListFragment getListFragment() {
        return listFragment;
    }
    public SettingsFragment getSettingsFragment() {
        return settingsFragment;
    }
    public DirectoryFragment getDirectoryFragment() {
        return directoryFragment;
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "This app requires Google Play Services",Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "This app requires Google Play Services");
                } else {
                    calAPI.getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        calAPI.mCredential.setSelectedAccount(new Account (accountName, "org.michiganhackers.michiganhackers"));
                        calAPI.getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    calAPI.getResultsFromApi();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewPager.getCurrentItem() != 0) {
                viewPager.setCurrentItem(0);
                return false;
            }
            else {
                return super.onKeyDown(keyCode,event);
            }
        }
        return super.onKeyDown(keyCode,event);
    }
 }