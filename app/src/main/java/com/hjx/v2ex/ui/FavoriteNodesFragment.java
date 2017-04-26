package com.hjx.v2ex.ui;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.FavoriteNodes;
import com.hjx.v2ex.bean.MemberTopicsPage;
import com.hjx.v2ex.bean.Node;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.flexibleitem.NodeFlexibleItem;
import com.hjx.v2ex.flexibleitem.ProgressItem;
import com.hjx.v2ex.flexibleitem.TopicFlexibleItem;
import com.hjx.v2ex.network.RetrofitSingleton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollGridLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteNodesFragment extends DataLoadingBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private FlexibleAdapter favoriteNodesAdapter;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    public int getContentRes() {
        return R.layout.recycler_view_layout;
    }

    @Override
    protected void initView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        favoriteNodesAdapter = new FlexibleAdapter(new ArrayList());
        recyclerView.setAdapter(favoriteNodesAdapter);
        recyclerView.setLayoutManager(new SmoothScrollGridLayoutManager(getContext(), 3));
    }

    @Override
    protected void loadData() {
        loadFavoriteNodes();
    }

    @Override
    public void onRefresh() {
        loadFavoriteNodes();
    }

    private void loadFavoriteNodes() {
        RetrofitSingleton.getInstance(getContext()).favoriteNodesPage().enqueue(new Callback<FavoriteNodes>() {
            @Override
            public void onResponse(Call<FavoriteNodes> call, Response<FavoriteNodes> response) {
                swipeRefreshLayout.setRefreshing(false);
                favoriteNodesAdapter.clear();
                FavoriteNodes favoriteNodes = response.body();
                if (favoriteNodes != null) {
                    successLoadingData();
                    for (Node node : favoriteNodes.getFavoriteNodes()) {
                        favoriteNodesAdapter.addItem(new NodeFlexibleItem(node, null));
                    }
                } else {
                    errorLoadingData();
                }
            }

            @Override
            public void onFailure(Call<FavoriteNodes> call, Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                favoriteNodesAdapter.clear();
                errorLoadingData();
                throwable.printStackTrace();
            }
        });
    }
}
