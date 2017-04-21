package com.hjx.v2ex.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.entity.Node;
import com.hjx.v2ex.entity.NodeDetailsFlexibleItem;
import com.hjx.v2ex.entity.NodePage;
import com.hjx.v2ex.entity.ProgressItem;
import com.hjx.v2ex.entity.Topic;
import com.hjx.v2ex.entity.TopicFlexibleItem;
import com.hjx.v2ex.network.RetrofitSingleton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NodeDetailsActivity extends AppCompatActivity implements FlexibleAdapter.EndlessScrollListener {

    private Node node;
    private FlexibleAdapter flexibleAdapter;
    private int currentPage = 1;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_layout);
        ButterKnife.bind(this);
        node = (Node) getIntent().getSerializableExtra("node");
        setTitle(node.getTitle());
        swipeRefreshLayout.setRefreshing(true);
        flexibleAdapter = new FlexibleAdapter(new ArrayList());
        recyclerView.setAdapter(flexibleAdapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(this));
        loadNodeDetails();
    }

    private void loadNodeDetails() {
        RetrofitSingleton.getInstance().nodeDetailsPage(node.getName(), currentPage).enqueue(new Callback<NodePage>() {
            @Override
            public void onResponse(Call<NodePage> call, Response<NodePage> response) {
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
                final NodePage nodePage = response.body();
                final List<TopicFlexibleItem> topics = new ArrayList<>();
                for (Topic topic : nodePage.getTopics().getCurrentPageItems()) {
                    topics.add(new TopicFlexibleItem(topic, TopicFlexibleItem.TopicItemType.NODE, null));
                }
                if (currentPage == 1) {
                    flexibleAdapter.addScrollableHeader(new NodeDetailsFlexibleItem(nodePage.getNode()));
                    flexibleAdapter.notifyDataSetChanged();
                    flexibleAdapter.addItems(flexibleAdapter.getItemCount(), topics);
                    if(nodePage.getTopics().getTotalPage() >=2) {
                        flexibleAdapter.setEndlessScrollListener(NodeDetailsActivity.this, new ProgressItem())
                                .setEndlessTargetCount(nodePage.getTopics().getTotalItems());
                    }
                } else {
                    flexibleAdapter.onLoadMoreComplete(topics, 5000);
                }
                currentPage++;
            }

            @Override
            public void onFailure(Call<NodePage> call, Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void noMoreLoad(int newItemsSize) {
        System.out.println(newItemsSize);
    }

    @Override
    public void onLoadMore(int lastPosition, int currentPage) {
        loadNodeDetails();
    }
}
