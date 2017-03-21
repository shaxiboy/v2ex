package com.hjx.v2ex.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.hjx.v2ex.R;
import com.hjx.v2ex.adapter.TopicAdapter;
import com.hjx.v2ex.entity.Reply;
import com.hjx.v2ex.entity.Topic;
import com.hjx.v2ex.network.V2EXServiceSingleton;
import com.hjx.v2ex.util.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private TopicAdapter topicAdapter;
    private int topicId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        topicAdapter = new TopicAdapter(this);
        recyclerView.setAdapter(topicAdapter);

        topicId = getIntent().getIntExtra("topicId", -1);
        loadTopicDetails();
        loadReplies();
    }

    private void loadTopicDetails() {
        V2EXServiceSingleton.getInstance().getTopic(topicId).enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                Topic topic = response.body().get(0);
                topicAdapter.setTopic(topic);
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable throwable) {
                LogUtil.d("onFailure");
            }
        });
    }

    private void loadReplies() {
        V2EXServiceSingleton.getInstance().getReplies(topicId, 1, 20).enqueue(new Callback<List<Reply>>() {
            @Override
            public void onResponse(Call<List<Reply>> call, Response<List<Reply>> response) {
                topicAdapter.addReplies(response.body());
            }

            @Override
            public void onFailure(Call<List<Reply>> call, Throwable throwable) {
                LogUtil.d("onFailure");
            }
        });
    }

}
