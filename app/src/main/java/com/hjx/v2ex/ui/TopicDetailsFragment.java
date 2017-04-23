package com.hjx.v2ex.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.flexibleitem.ProgressItem;
import com.hjx.v2ex.bean.Reply;
import com.hjx.v2ex.flexibleitem.TopicDetailsFlexibleItem;
import com.hjx.v2ex.bean.TopicPage;
import com.hjx.v2ex.flexibleitem.TopicReplyFlexibleItem;
import com.hjx.v2ex.network.RetrofitSingleton;
import com.hjx.v2ex.util.LogUtil;

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
public class TopicDetailsFragment extends DataLoadingBaseFragment implements SwipeRefreshLayout.OnRefreshListener, FlexibleAdapter.EndlessScrollListener {

    private int topicId;
    private FlexibleAdapter topicDetailsAdapter;
    private int currentPage = 1;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static TopicDetailsFragment newInstance(String topicId) {
        TopicDetailsFragment topicDetailsFragment = new TopicDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_TOPIC, Integer.parseInt(topicId));
        topicDetailsFragment.setArguments(bundle);
        return topicDetailsFragment;
    }

    @Override
    public int getContentRes() {
        return R.layout.recycler_view_layout;
    }

    @Override
    protected void initView() {
        topicId = getArguments().getInt(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_TOPIC);
        swipeRefreshLayout.setOnRefreshListener(this);
        topicDetailsAdapter = new FlexibleAdapter(new ArrayList());
        recyclerView.setAdapter(topicDetailsAdapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext()));
    }

    @Override
    protected void loadData() {
        loadTopicDetails();
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        loadTopicDetails();
    }

    private void loadTopicDetails() {
        RetrofitSingleton.getInstance().topicPage(topicId, currentPage).enqueue(new Callback<TopicPage>() {
            @Override
            public void onResponse(Call<TopicPage> call, Response<TopicPage> response) {
                swipeRefreshLayout.setRefreshing(false);
                TopicPage topicPage = response.body();
                if (topicPage != null) {
                    LogUtil.d(topicPage.toString());
                    List<TopicReplyFlexibleItem> topicReplies = new ArrayList<>();
                    for (Reply reply : topicPage.getReplies().getCurrentPageItems()) {
                        topicReplies.add(new TopicReplyFlexibleItem(reply));
                    }
                    if (currentPage == 1) {
                        if (topicPage.getTopic() != null) {
                            successLoadingData();
                            topicDetailsAdapter.clear();
                            topicDetailsAdapter.addScrollableHeader(new TopicDetailsFlexibleItem(topicPage.getTopic()));
                            topicDetailsAdapter.notifyDataSetChanged();
                            topicDetailsAdapter.addItems(topicDetailsAdapter.getItemCount(), topicReplies);
                            if (topicPage.getReplies().getTotalPage() >= 2) {
                                topicDetailsAdapter.setEndlessScrollListener(TopicDetailsFragment.this, new ProgressItem())
                                        .setEndlessTargetCount(topicPage.getReplies().getTotalItems());
                            }
                        } else {
                            errorLoadingData();
                            return;
                        }

                    } else {
                        topicDetailsAdapter.onLoadMoreComplete(topicReplies, 5000);
                    }
                    if (!topicPage.getReplies().getCurrentPageItems().isEmpty()) {
                        currentPage++;
                    }
                } else {
                    if (currentPage == 1) {
                        errorLoadingData();
                    }
                }
            }

            @Override
            public void onFailure(Call<TopicPage> call, Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                if (currentPage == 1) {
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
        loadTopicDetails();
    }
}
