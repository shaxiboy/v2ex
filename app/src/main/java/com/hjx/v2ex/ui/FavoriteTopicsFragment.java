package com.hjx.v2ex.ui;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.MemberTopicsPage;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.flexibleitem.ProgressItem;
import com.hjx.v2ex.flexibleitem.TopicFlexibleItem;
import com.hjx.v2ex.network.RetrofitSingleton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteTopicsFragment extends DataLoadingBaseFragment implements SwipeRefreshLayout.OnRefreshListener, FlexibleAdapter.EndlessScrollListener {

    private FlexibleAdapter favoriteTopicsAdapter;
    private int currentPage = 1;

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
        favoriteTopicsAdapter = new FlexibleAdapter(new ArrayList());
        recyclerView.setAdapter(favoriteTopicsAdapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext()));
    }

    @Override
    protected void loadData() {
        loadFavoriteTopics();
    }

    @Override
    public void onRefresh() {
        loadFavoriteTopics();
    }

    private void loadFavoriteTopics() {
        RetrofitSingleton.getInstance(getContext()).favoriteTopicsPage(currentPage).enqueue(new Callback<MemberTopicsPage>() {
            @Override
            public void onResponse(Call<MemberTopicsPage> call, Response<MemberTopicsPage> response) {
                swipeRefreshLayout.setRefreshing(false);
                MemberTopicsPage memberTopicsPage = response.body();
                if (memberTopicsPage != null) {
                    List<TopicFlexibleItem> memberTopics = new ArrayList<>();
                    for(Topic topic : memberTopicsPage.getTopics().getCurrentPageItems()) {
                        memberTopics.add(new TopicFlexibleItem(topic, TopicFlexibleItem.TopicItemType.MEMBER, null));
                    }
                    if (currentPage == 1) {
                        favoriteTopicsAdapter.clear();
                        if (memberTopicsPage.getTopics() != null) {
                            successLoadingData();
                            favoriteTopicsAdapter.addItems(0, memberTopics);
                            if (memberTopicsPage.getTopics().getTotalPage() >= 2) {
                                favoriteTopicsAdapter.setEndlessScrollListener(FavoriteTopicsFragment.this, new ProgressItem())
                                        .setEndlessTargetCount(memberTopicsPage.getTopics().getTotalItems());
                            }
                        } else {
                            errorLoadingData();
                            return;
                        }

                    } else {
                        favoriteTopicsAdapter.onLoadMoreComplete(memberTopics, 5000);
                    }
                    if (!memberTopicsPage.getTopics().getCurrentPageItems().isEmpty()) {
                        currentPage++;
                    }
                } else {
                    if (currentPage == 1) {
                        favoriteTopicsAdapter.clear();
                        errorLoadingData();
                    }
                }
            }

            @Override
            public void onFailure(Call<MemberTopicsPage> call, Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                if (currentPage == 1) {
                    favoriteTopicsAdapter.clear();
                    errorLoadingData();
                }
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void noMoreLoad(int newItemsSize) {

    }

    @Override
    public void onLoadMore(int lastPosition, int currentPage) {
        loadFavoriteTopics();
    }
}
