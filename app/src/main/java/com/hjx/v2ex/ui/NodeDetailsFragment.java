package com.hjx.v2ex.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.flexibleitem.NodeDetailsFlexibleItem;
import com.hjx.v2ex.bean.NodePage;
import com.hjx.v2ex.flexibleitem.ProgressItem;
import com.hjx.v2ex.bean.Topic;
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
public class NodeDetailsFragment extends DataLoadingBaseFragment implements SwipeRefreshLayout.OnRefreshListener, FlexibleAdapter.EndlessScrollListener {

    private String nodeName;
    private FlexibleAdapter nodeDetailsAdapter;
    private int currentPage = 1;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static NodeDetailsFragment newInstance(String nodeName) {
        NodeDetailsFragment topicDetailsFragment = new NodeDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_NODE, nodeName);
        topicDetailsFragment.setArguments(bundle);
        return topicDetailsFragment;
    }

    @Override
    public int getContentRes() {
        return R.layout.recycler_view_layout;
    }

    @Override
    protected void initView() {
        nodeName = getArguments().getString(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_NODE);
        swipeRefreshLayout.setOnRefreshListener(this);
        nodeDetailsAdapter = new FlexibleAdapter(new ArrayList());
        recyclerView.setAdapter(nodeDetailsAdapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext()));
    }

    @Override
    protected void loadData() {
        loadNodeDetails();
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        loadNodeDetails();
    }


    private void loadNodeDetails() {
        RetrofitSingleton.getInstance(getContext()).nodeDetailsPage(nodeName, currentPage).enqueue(new Callback<NodePage>() {
            @Override
            public void onResponse(Call<NodePage> call, Response<NodePage> response) {
                swipeRefreshLayout.setRefreshing(false);
                NodePage nodePage = response.body();
                if(nodePage != null) {
                    List<TopicFlexibleItem> topics = new ArrayList<>();
                    for (Topic topic : nodePage.getTopics().getCurrentPageItems()) {
                        topics.add(new TopicFlexibleItem(topic, TopicFlexibleItem.TopicItemType.NODE, null));
                    }
                    if (currentPage == 1) {
                        if(nodePage.getNode() != null) {
                            successLoadingData();
                            nodeDetailsAdapter.clear();
                            nodeDetailsAdapter.addScrollableHeader(new NodeDetailsFlexibleItem(nodePage.getNode()));
                            nodeDetailsAdapter.notifyDataSetChanged();
                            nodeDetailsAdapter.addItems(nodeDetailsAdapter.getItemCount(), topics);
                            if(nodePage.getTopics().getTotalPage() >=2) {
                                nodeDetailsAdapter.setEndlessScrollListener(NodeDetailsFragment.this, new ProgressItem())
                                        .setEndlessTargetCount(nodePage.getTopics().getTotalItems());
                            }
                        } else {
                            errorLoadingData();
                            return;
                        }
                    } else {
                        nodeDetailsAdapter.onLoadMoreComplete(topics, 5000);
                    }
                    if (!nodePage.getTopics().getCurrentPageItems().isEmpty()) {
                        currentPage++;
                    }
                } else {
                    if (currentPage == 1) {
                        errorLoadingData();
                    }
                }
            }

            @Override
            public void onFailure(Call<NodePage> call, Throwable throwable) {
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
        loadNodeDetails();
    }
}
