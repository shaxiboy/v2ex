package com.hjx.v2ex.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.HomePage;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.flexibleitem.TopicFlexibleItem;
import com.hjx.v2ex.network.RetrofitSingleton;

import java.util.ArrayList;

import butterknife.BindView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shaxiboy on 2017/3/6 0006.
 */

public class TopicListFragment extends ListBaseFragment {

    private String tab;

    public static TopicListFragment newInstance(String tab) {
        TopicListFragment topicListFragment = new TopicListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tab", tab);
        topicListFragment.setArguments(bundle);
        return topicListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tab = getArguments().getString("tab");
    }

    @Override
    protected void loadData() {
        RetrofitSingleton.getInstance(getContext()).getTabTopics(tab).enqueue(getListBaseFragmentCallBack());
    }

    @Override
    AbstractFlexibleItem getFlexibleItem(Object item) {
        return new TopicFlexibleItem((Topic) item);
    }
}
