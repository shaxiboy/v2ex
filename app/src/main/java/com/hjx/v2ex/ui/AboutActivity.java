package com.hjx.v2ex.ui;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.entity.HomePage;
import com.hjx.v2ex.entity.NodesPlane;
import com.hjx.v2ex.entity.V2EX;
import com.hjx.v2ex.entity.V2EXMoreInfo;
import com.hjx.v2ex.network.RetrofitSingleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.moreInfo)
    TextView moreInfo;
    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.member)
    TextView member;
    @BindView(R.id.node)
    TextView node;
    @BindView(R.id.topic)
    TextView topic;
    @BindView(R.id.reply)
    TextView reply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        RetrofitSingleton.getInstance().homePage(null).enqueue(new Callback<HomePage>() {
            @Override
            public void onResponse(Call<HomePage> call, Response<HomePage> response) {
                progressBar.setVisibility(View.INVISIBLE);
                V2EX v2ex = response.body().getV2ex();
                member.setText(v2ex.getMembers() + "");
                topic.setText(v2ex.getTopics() + "");
                reply.setText(v2ex.getReplies() + "");
            }

            @Override
            public void onFailure(Call<HomePage> call, Throwable throwable) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        RetrofitSingleton.getInstance().allNodesPage().enqueue(new Callback<NodesPlane>() {
            @Override
            public void onResponse(Call<NodesPlane> call, Response<NodesPlane> response) {
                progressBar.setVisibility(View.INVISIBLE);
                node.setText(response.body().getNodeCount() + "");
            }

            @Override
            public void onFailure(Call<NodesPlane> call, Throwable throwable) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void showMore(View view) {
        constraintLayout.setVisibility(View.INVISIBLE);
        scrollView.setVisibility(View.VISIBLE);
        RetrofitSingleton.getInstance().aboutPage().enqueue(new Callback<V2EXMoreInfo>() {
            @Override
            public void onResponse(Call<V2EXMoreInfo> call, Response<V2EXMoreInfo> response) {
                moreInfo.setText(response.body().getMoreInfo());
            }

            @Override
            public void onFailure(Call<V2EXMoreInfo> call, Throwable throwable) {
                moreInfo.setText("加载失败");
            }
        });
    }
}
