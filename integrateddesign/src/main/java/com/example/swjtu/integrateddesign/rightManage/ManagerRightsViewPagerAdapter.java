package com.example.swjtu.integrateddesign.rightManage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class ManagerRightsViewPagerAdapter extends FragmentPagerAdapter {

    List<RightsFragment> rightsFragments;
    List<String> tabTitles;

    public ManagerRightsViewPagerAdapter(FragmentManager fm, List<RightsFragment> rightsFragments, List<String> tabTitles) {
        super(fm);
        this.rightsFragments = rightsFragments;
        this.tabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return rightsFragments.get(position);
    }

    @Override
    public int getCount() {
        return rightsFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }
}
