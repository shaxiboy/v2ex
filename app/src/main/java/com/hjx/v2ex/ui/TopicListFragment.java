package com.hjx.v2ex.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hjx.v2ex.bean.PageData;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.bean.TopicsPageData;
import com.hjx.v2ex.flexibleitem.TopicFlexibleItem;
import com.hjx.v2ex.network.RetrofitSingleton;

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

/**
 * Created by shaxiboy on 2017/3/6 0006.
 */

public class TopicListFragment extends ListBaseFragment<TopicsPageData> {

    public static final String TOPICTYPE_TABTOPIC = "TABTOPIC";
    public static final String TOPICTYPE_RECENTTOPIC = "RECENTTOPIC";
    public static final String TOPICTYPE_FAVORITETOPIC = "FAVORITETOPIC";
    public static final String TOPICTYPE_MEMBERTOPIC = "MEMBERTOPIC";
    public static final String TOPICTYPE_FAVORITEMEMBERSTOPIC = "FAVORITEMEMBERSTOPIC";
    private static final String TAG = TopicListFragment.class.getSimpleName();
    private String topicType;
    private String arg;

    public static TopicListFragment newInstance(String topicType) {
        TopicListFragment topicListFragment = new TopicListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DataLoadingBaseActivity.ARG_TOPICTYPE, topicType);
        topicListFragment.setArguments(bundle);
        return topicListFragment;
    }

    public static TopicListFragment newInstance(String topicType, String tabName) {
        TopicListFragment topicListFragment = new TopicListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DataLoadingBaseActivity.ARG_TOPICTYPE, topicType);
        bundle.putString(DataLoadingBaseActivity.ARG_TABNAME, tabName);
        topicListFragment.setArguments(bundle);
        return topicListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arg = getArguments().getString(DataLoadingBaseActivity.ARG_TABNAME);
        topicType = getArguments().getString(DataLoadingBaseActivity.ARG_TOPICTYPE);
    }

    @Override
    protected void loadData() {
        switch (topicType) {
            case TOPICTYPE_TABTOPIC:
                RetrofitSingleton.getInstance(getContext()).getTabTopics(arg).enqueue(getListBaseFragmentCallBack());
                break;
            case TOPICTYPE_RECENTTOPIC:
                RetrofitSingleton.getInstance(getContext()).getRecentTopics(getCurrentPage()).enqueue(getListBaseFragmentCallBack());
                break;
            case TOPICTYPE_FAVORITETOPIC:
                RetrofitSingleton.getInstance(getContext()).getFavoriteTopics(getCurrentPage()).enqueue(getListBaseFragmentCallBack());
                break;
            case TOPICTYPE_MEMBERTOPIC:
                RetrofitSingleton.getInstance(getContext()).getMemberTopics(arg, getCurrentPage()).enqueue(getListBaseFragmentCallBack());
                break;
            case TOPICTYPE_FAVORITEMEMBERSTOPIC:
                RetrofitSingleton.getInstance(getContext()).getFavoriteMembersTopics(getCurrentPage()).enqueue(getListBaseFragmentCallBack());
        }
    }

    @Override
    ListData getListData(TopicsPageData data) {
        ListData listData = new ListData();
        PageData<AbstractFlexibleItem> pageData = new PageData<>();
        copyPageDataStatistics(data.getTopics(), pageData);
        for(Topic topic : data.getTopics().getCurrentPageItems()) {
            pageData.getCurrentPageItems().add(new TopicFlexibleItem(topic));
        }
        listData.setPageData(pageData);
        return listData;
    }
}
