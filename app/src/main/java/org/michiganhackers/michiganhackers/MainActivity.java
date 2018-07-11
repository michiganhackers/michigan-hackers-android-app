package org.michiganhackers.michiganhackers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private BottomNavigationView mainNav;
    private android.view.MenuItem prevMenuItem;

    public static ListFragment listFragment;
    private CalendarFragment calendarFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalenderAPI calAPI = new CalenderAPI(this, this, savedInstanceState);

        calAPI.getResultsFromApi();

        final ViewPager mainPager = (ViewPager) findViewById(R.id.main_pager);
        FragmentPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        mainPager.setAdapter(mainPagerAdapter);

        mainNav = findViewById(R.id.main_nav);

        listFragment = new ListFragment();
        calendarFragment = new CalendarFragment();
        settingsFragment = new SettingsFragment();

        // Replace current fragment with one corresponding to which navigation item is selected
        mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_list:
                        mainPager.setCurrentItem(0);
                        return true;
                    case R.id.nav_calendar:
                        mainPager.setCurrentItem(1);
                        return true;
                    case R.id.nav_settings:
                        mainPager.setCurrentItem(2);
                        return true;
                    default:
                        return false;
                }
            }
        });
        mainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    mainNav.getMenu().getItem(0).setChecked(false);
                }
                mainNav.getMenu().getItem(position).setChecked(true);
                prevMenuItem = mainNav.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state){

            }
        });


    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }
    // Todo: Bundles should only hold a small amount of data. Change to viewmodel
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public ListFragment getListFragment() {
        return listFragment;
    }

    public CalendarFragment getCalendarFragment() {
        return calendarFragment;
    }

    public SettingsFragment getSettingsFragment() {
        return settingsFragment;
    }
}


