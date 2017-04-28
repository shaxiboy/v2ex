package com.hjx.v2ex.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hjx.v2ex.ui.TopicListFragment;

/**
 * Created by shaxiboy on 2017/3/6 0006.
 */

public class TopicsPagerAdapter extends FragmentStatePagerAdapter {

    private String[] tabs = new String[] {"tech", "creative", "play", "apple", "jobs", "deals", "city", "qna", "hot", "all", "r2"};
    private String[] tabnames = new String[] {"技术", "创意", "好玩", "Apple", "酷工作", "交易", "城市", "问与答", "最热", "全部", "R2"};

    public TopicsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TopicListFragment.newInstance(TopicListFragment.TOPICTYPE_TABTOPIC, tabs[position]);
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabnames[position];
    }
}
