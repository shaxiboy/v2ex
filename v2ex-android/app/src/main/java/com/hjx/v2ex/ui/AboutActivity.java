package com.hjx.v2ex.ui;

import android.content.pm.PackageManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.V2EXStatistics;
import com.hjx.v2ex.bean.V2EXIntroduction;
import com.hjx.v2ex.flexibleitem.TopicFlexibleItem;
import com.hjx.v2ex.network.RetrofitServiceSingleton;
import com.hjx.v2ex.util.V2EXUtil;
import com.tencent.bugly.beta.Beta;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutActivity extends AppCompatActivity {

    private V2EXStatistics statistics;

    @BindView(R.id.versionTV)
    TextView versionTV;
    @BindView(R.id.check_versionTV)
    TextView checkVersionTV;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.introduction)
    TextView introduction;
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
        String appVersion = null;
        try {
            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionTV.setText("版本号：" + appVersion);
        checkVersionTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Beta.checkUpgrade();
            }
        });
        RetrofitServiceSingleton.getInstance(getApplication()).getV2EXStatistics().enqueue(new Callback<V2EXStatistics>() {
            @Override
            public void onResponse(Call<V2EXStatistics> call, Response<V2EXStatistics> response) {
                V2EXStatistics data = response.body();
                if (data == null) data = new V2EXStatistics();
                setStatisticsView(data);
            }

            @Override
            public void onFailure(Call<V2EXStatistics> call, Throwable throwable) {
                setStatisticsView(new V2EXStatistics());
                throwable.printStackTrace();
            }
        });

        RetrofitServiceSingleton.getInstance(getApplication()).getV2EXNodesSum().enqueue(new Callback<V2EXStatistics>() {
            @Override
            public void onResponse(Call<V2EXStatistics> call, Response<V2EXStatistics> response) {
                V2EXStatistics data = response.body();
                if (data == null) data = new V2EXStatistics();
                setStatisticsView(data);
            }

            @Override
            public void onFailure(Call<V2EXStatistics> call, Throwable throwable) {
                setStatisticsView(new V2EXStatistics());
                throwable.printStackTrace();
            }
        });
    }

    public void setStatisticsView(V2EXStatistics data) {
        if (statistics != null) {
            if (statistics.getTopics() == 0) statistics.setTopics(data.getTopics());
            if (statistics.getMembers() == 0) statistics.setMembers(data.getMembers());
            if (statistics.getReplies() == 0) statistics.setReplies(data.getReplies());
            if (statistics.getNodes() == 0) statistics.setNodes(data.getNodes());
            member.setText(statistics.getMembers() + "");
            topic.setText(statistics.getTopics() + "");
            reply.setText(statistics.getReplies() + "");
            node.setText(statistics.getNodes() + "");
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            statistics = data;
        }
    }

    public void showMore(View view) {
        constraintLayout.setVisibility(View.INVISIBLE);
        scrollView.setVisibility(View.VISIBLE);
        RetrofitServiceSingleton.getInstance(getApplication()).getV2EXIntroduction().enqueue(new Callback<V2EXIntroduction>() {
            @Override
            public void onResponse(Call<V2EXIntroduction> call, Response<V2EXIntroduction> response) {
                introduction.setText(V2EXUtil.fromHtml(response.body().getIntroduction(), null, null, -1));
            }

            @Override
            public void onFailure(Call<V2EXIntroduction> call, Throwable throwable) {
                introduction.setText("加载失败");
            }
        });
    }

    public void goToAuthor(View view) {
        DataLoadingBaseActivity.gotoMemberDetailsActivity(view.getContext(), "shaxiboy");
    }
}
