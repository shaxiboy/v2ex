package com.hjx.v2ex.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hjx.v2ex.ui.TopicListFragment;
import com.hjx.v2ex.util.V2EXUtil;

/**
 * Created by shaxiboy on 2017/3/6 0006.
 */

public class TopicsPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private String[] tabs = new String[] {"tech", "creative", "play", "apple", "jobs", "deals", "city", "qna", "hot", "all", "r2", "nodes", "members", "recent"};
    private String[] tabnames = new String[] {"技术", "创意", "好玩", "Apple", "酷工作", "交易", "城市", "问与答", "最热", "全部", "R2", "节点", "关注", "最近"};

    public TopicsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(tabs[position].equals("recent")) return TopicListFragment.newInstance(TopicListFragment.TOPICTYPE_RECENTTOPIC);
        else return TopicListFragment.newInstance(TopicListFragment.TOPICTYPE_TABTOPIC, tabs[position]);
    }

    @Override
    public int getCount() {
        if(V2EXUtil.isLogin(context)) return tabs.length;
        else return tabs.length - 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabnames[position];
    }
}
