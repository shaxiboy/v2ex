package com.hjx.v2ex.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hjx.v2ex.ui.NodeListFragment;

/**
 * Created by shaxiboy on 2017/4/12 0012.
 */

public class NodesPagerAdapter extends FragmentStatePagerAdapter {

    private String nodeActionType;
    private String[] tabs = new String[] {"最热", "导航", "全部"};

    public NodesPagerAdapter(FragmentManager fm, String nodeActionType) {
        super(fm);
        this.nodeActionType = nodeActionType;
    }
    @Override
    public Fragment getItem(int position) {
        return NodeListFragment.newInstance(tabs[position], nodeActionType);
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }
}
