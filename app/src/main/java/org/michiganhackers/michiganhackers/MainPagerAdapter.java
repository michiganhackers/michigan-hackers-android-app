package org.michiganhackers.michiganhackers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {
    private int NUM_ITEMS = 3;
    private MainActivity mainActivity;

    public MainPagerAdapter(FragmentManager fragmentManager, MainActivity mainActivity) {
        super(fragmentManager);
        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mainActivity.getListFragment();
            case 1:
                return mainActivity.getDirectoryFragment();
            case 2:
                return mainActivity.getSettingsFragment();
            default:
                return null;
        }
    }
}
