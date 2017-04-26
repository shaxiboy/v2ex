package com.hjx.v2ex.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hjx.v2ex.ui.FavoriteMembersFragment;
import com.hjx.v2ex.ui.FavoriteNodesFragment;
import com.hjx.v2ex.ui.FavoriteTopicsFragment;

/**
 * Created by shaxiboy on 2017/3/6 0006.
 */

public class MyFavoritesPagerAdapter extends FragmentStatePagerAdapter {

    private String[] tabnames = new String[] {"主题", "会员", "节点"};

    public MyFavoritesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(tabnames[position].equals("主题")) return new FavoriteTopicsFragment();
        else if(tabnames[position].equals("会员")) return new FavoriteMembersFragment();
        else return  new FavoriteNodesFragment();
    }

    @Override
    public int getCount() {
        return tabnames.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabnames[position];
    }
}
