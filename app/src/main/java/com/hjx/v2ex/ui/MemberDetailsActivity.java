package com.hjx.v2ex.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.entity.Member;
import com.hjx.v2ex.entity.MemberDetailsFlexibleItem;
import com.hjx.v2ex.entity.MemberReplyFlexibleItem;
import com.hjx.v2ex.entity.MemberTopicRepliesPage;
import com.hjx.v2ex.entity.MemberTopicsPage;
import com.hjx.v2ex.entity.NodeCategoryFlexibleHeaderItem;
import com.hjx.v2ex.entity.Reply;
import com.hjx.v2ex.entity.SimpleFlexibleHeaderItem;
import com.hjx.v2ex.entity.Topic;
import com.hjx.v2ex.entity.TopicFlexibleItem;
import com.hjx.v2ex.entity.ViewMoreFlexibleItem;
import com.hjx.v2ex.network.RetrofitSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberDetailsActivity extends AppCompatActivity {

    private String memberName;
    private FlexibleAdapter flexibleAdapter;
    private Member member;
    private List<Topic> topics = new ArrayList<>();
    private List<Map<Reply, Topic>> replies = new ArrayList<>();

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_layout);
        ButterKnife.bind(this);
        memberName = getIntent().getStringExtra("member");
        swipeRefreshLayout.setRefreshing(true);
        FlexibleAdapter.enableLogs(true);
        flexibleAdapter = new FlexibleAdapter(new ArrayList());
        flexibleAdapter.setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true);
        recyclerView.setAdapter(flexibleAdapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(this));
        loadMemberDetails();
        loadMemberTopics();
        loadMemberReplies();
    }

    private void loadMemberDetails() {
        RetrofitSingleton.getInstance().memberDetailsPage(memberName).enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
                member = response.body();
                flexibleAdapter.addScrollableHeader(new MemberDetailsFlexibleItem(member));
            }

            @Override
            public void onFailure(Call<Member> call, Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                throwable.printStackTrace();
            }
        });
    }

    private void loadMemberTopics() {
        RetrofitSingleton.getInstance().memberTopicsPage(memberName, 1).enqueue(new Callback<MemberTopicsPage>() {
            @Override
            public void onResponse(Call<MemberTopicsPage> call, Response<MemberTopicsPage> response) {
                if(member != null) {
                    topics = response.body().getTopics().getCurrentPageItems();
                    List<AbstractFlexibleItem> topicFlexibleItems = new ArrayList<>();
                    SimpleFlexibleHeaderItem headerItem = new SimpleFlexibleHeaderItem(memberName + "发表的主题");
                    int i = 0;
                    for(Topic topic : topics) {
                        if(++i > 5) {
                            break;
                        }
                        topicFlexibleItems.add(new TopicFlexibleItem(topic, TopicFlexibleItem.TopicItemType.MEMBER, headerItem));
//                        headerItem.addSubItem(new TopicFlexibleItem(topic, headerItem));
                    }
                    if(topics.size() > 5) {
                        topicFlexibleItems.add(new ViewMoreFlexibleItem(memberName, "topic"));
                    }
                    flexibleAdapter.addItems(1, topicFlexibleItems);
//                    flexibleAdapter.addItem(headerItem);
                }
            }

            @Override
            public void onFailure(Call<MemberTopicsPage> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void loadMemberReplies() {
        RetrofitSingleton.getInstance().memberTopicRepliesPage(memberName, 1).enqueue(new Callback<MemberTopicRepliesPage>() {
            @Override
            public void onResponse(Call<MemberTopicRepliesPage> call, Response<MemberTopicRepliesPage> response) {
                if(topics != null) {
                    replies = response.body().getReplies().getCurrentPageItems();
                    List<AbstractFlexibleItem> replyFlexibleItems = new ArrayList<>();
                    SimpleFlexibleHeaderItem headerItem = new SimpleFlexibleHeaderItem(memberName + "发表的回复");
                    int i = 0;
                    for(Map<Reply, Topic> replyMap : replies) {
                        if(++i > 5) {
                            break;
                        }
                        replyFlexibleItems.add(new MemberReplyFlexibleItem(replyMap, headerItem));
                    }
                    if(replies.size() > 5) {
                        replyFlexibleItems.add(new ViewMoreFlexibleItem(memberName, "reply"));
                    }
                    flexibleAdapter.addItems(flexibleAdapter.getItemCount(), replyFlexibleItems);
                }
            }

            @Override
            public void onFailure(Call<MemberTopicRepliesPage> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
