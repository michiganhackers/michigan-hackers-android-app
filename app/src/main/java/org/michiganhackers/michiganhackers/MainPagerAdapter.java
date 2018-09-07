package org.michiganhackers.michiganhackers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class MainPagerAdapter extends FragmentPagerAdapter {
    private final MainActivity mainActivity;

    MainPagerAdapter(FragmentManager fragmentManager, MainActivity mainActivity) {
        super(fragmentManager);
        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return 3;
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
