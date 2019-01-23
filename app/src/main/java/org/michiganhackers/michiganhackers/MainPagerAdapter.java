package org.michiganhackers.michiganhackers;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
                return mainActivity.getMainSettingsFragment();
            default:
                return null;
        }
    }
}
