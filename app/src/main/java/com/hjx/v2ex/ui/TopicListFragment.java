package com.hjx.v2ex.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hjx.v2ex.R;
import com.hjx.v2ex.adapter.TopicListAdapter;
import com.hjx.v2ex.entity.HomePage;
import com.hjx.v2ex.network.RetrofitSingleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static butterknife.ButterKnife.bind;

/**
 * Created by shaxiboy on 2017/3/6 0006.
 */

public class TopicListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private String tab;
    private TopicListAdapter adapter;
    private boolean hasCreateView;
    private boolean hasLoadData;
    private boolean isVisibleToUser;
    private Unbinder unbinder;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    public static Fragment newInstance(String tab) {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.topic_list, container, false);
        unbinder = ButterKnife.bind(this, root);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TopicListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        hasCreateView = true;
        if (isVisibleToUser) {
            loadTopics();
        }
        return root;
    }

    //数据懒加载
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && hasCreateView && !hasLoadData) {
            loadTopics();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        loadTopics();
    }

    //需实现数据的懒加载
    public void loadTopics() {
        swipeRefreshLayout.setRefreshing(true);
        RetrofitSingleton.getInstance().homePage(tab).enqueue(new Callback<HomePage>() {
            @Override
            public void onResponse(Call<HomePage> call, Response<HomePage> response) {
                swipeRefreshLayout.setRefreshing(false);
                HomePage homePage = response.body();
                if (homePage != null) {
                    adapter.setTopics(homePage.getTopics());
                    hasLoadData = true;
                }
            }

            @Override
            public void onFailure(Call<HomePage> call, Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                throwable.printStackTrace();
            }
        });
    }
}
