package com.hjx.v2ex.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.hjx.v2ex.R;
import com.hjx.v2ex.adapter.OnScrollToBottomListener;
import com.hjx.v2ex.adapter.TopicAdapter;
import com.hjx.v2ex.entity.PageData;
import com.hjx.v2ex.entity.Reply;
import com.hjx.v2ex.entity.Topic;
import com.hjx.v2ex.entity.TopicPage;
import com.hjx.v2ex.network.RetrofitSingleton;
import com.hjx.v2ex.util.HTMLUtil;
import com.hjx.v2ex.util.LogUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicDetailsActivity extends AppCompatActivity implements OnScrollToBottomListener, SwipeRefreshLayout.OnRefreshListener {

    private TopicAdapter topicAdapter;
    private int topicId;
    private int currentPage = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        topicAdapter = new TopicAdapter(this, this);
        recyclerView.setAdapter(topicAdapter);

        topicId = getIntent().getIntExtra("topicId", -1);
        loadTopicDetails();
        loadReplies();
    }

    private void loadTopicDetails() {
        RetrofitSingleton.getInstance().topicPage(topicId, null).enqueue(new Callback<TopicPage>() {
            @Override
            public void onResponse(Call<TopicPage> call, Response<TopicPage> response) {
                swipeRefreshLayout.setRefreshing(false);
                Topic topic = response.body().getTopic();
                LogUtil.d(topic.toString());
                topicAdapter.setTopic(topic);
            }

            @Override
            public void onFailure(Call<TopicPage> call, Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                throwable.printStackTrace();
            }
        });
    }

    private void loadReplies() {
        RetrofitSingleton.getInstance().topicPage(topicId, currentPage).enqueue(new Callback<TopicPage>() {
            @Override
            public void onResponse(Call<TopicPage> call, Response<TopicPage> response) {
                swipeRefreshLayout.setEnabled(true);
                swipeRefreshLayout.setRefreshing(false);
                PageData<Reply> pageData = response.body().getReplies();
                LogUtil.d(pageData.toString());
                if (pageData.getCurrentPageItems().size() > 0) {
                    currentPage++;
                    topicAdapter.setPageData(pageData);
                }
            }

            @Override
            public void onFailure(Call<TopicPage> call, Throwable throwable) {
                swipeRefreshLayout.setEnabled(true);
                swipeRefreshLayout.setRefreshing(false);
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void onLoadMore() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setEnabled(false);
            loadReplies();
        }
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        loadTopicDetails();
        loadReplies();
    }
}
