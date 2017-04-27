package com.hjx.v2ex.ui;

import android.os.Bundle;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shaxiboy on 2017/3/6 0006.
 */

public class TopicListFragment extends DataLoadingBaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    private String tab;
    private FlexibleAdapter<TopicFlexibleItem> adapter;
    private boolean hasCreateView;
    private boolean hasLoadData;
    private boolean isVisibleToUser;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static TopicListFragment newInstance(String tab) {
        TopicListFragment topicListFragment = new TopicListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tab", tab);
        topicListFragment.setArguments(bundle);
        return topicListFragment;
    }

    @Override
    public int getContentRes() {
        return R.layout.fragment_topic_list;
    }

    @Override
    protected void initView() {
        tab = getArguments().getString("tab");
        adapter = new FlexibleAdapter<>(new ArrayList<TopicFlexibleItem>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext()));
        swipeRefreshLayout.setOnRefreshListener(this);
        hasCreateView = true;
    }

    @Override
    protected void loadData() {
//        if (isVisibleToUser) {
            loadTopics();
//        }
    }

    //数据懒加载
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && hasCreateView && !hasLoadData) {
//            loadTopics();
        }
    }

    @Override
    public void onRefresh() {
        loadTopics();
    }

    //需实现数据的懒加载
    public void loadTopics() {
        RetrofitSingleton.getInstance(getContext()).homePage(tab).enqueue(new Callback<HomePage>() {
            @Override
            public void onResponse(Call<HomePage> call, Response<HomePage> response) {
                swipeRefreshLayout.setRefreshing(false);
                HomePage homePage = response.body();
                if (homePage != null) {
                    for(Topic topic : homePage.getTopics()) {
                        adapter.addItem(new TopicFlexibleItem(topic, TopicFlexibleItem.TopicItemType.FULL, null));
                    }
                    successLoadingData();
                    hasLoadData = true;
                } else {
                    errorLoadingData();
                }
            }

            @Override
            public void onFailure(Call<HomePage> call, Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                errorLoadingData();
                throwable.printStackTrace();
            }
        });
    }
}
