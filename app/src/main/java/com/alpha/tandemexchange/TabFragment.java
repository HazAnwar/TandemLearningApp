package com.alpha.tandemexchange;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Fragment to manage the Tabs
 */
public class TabFragment extends FragmentStatePagerAdapter {

    /**
     * Field containing the number of Tabs
     */
    int noOfTabs;

    /**
     * Instantiates the fragment
     * @param fragmentManager is the manager of the fragment
     * @param noOfTabs is the number of Tabs
     */
    public TabFragment(FragmentManager fragmentManager, int noOfTabs) {
        super(fragmentManager);
        this.noOfTabs = noOfTabs;
    }

    /**
     * Method to switch between the Tabs and return the corresponding Fragment
     * @param position is position of the Tab of the Fragment that is currently selected
     * @return returns the Fragment for the selected tab
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ProfileFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new MessageFragment();
            default:
                return null;
        }
    }

    /**
     * Method to return the number of Tabs
     * @return
     */
    @Override
    public int getCount() {
        return noOfTabs;
    }
}