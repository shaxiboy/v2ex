package com.hjx.v2ex.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.Member;
import com.hjx.v2ex.flexibleitem.MemberDetailsFlexibleItem;
import com.hjx.v2ex.flexibleitem.MemberReplyFlexibleItem;
import com.hjx.v2ex.bean.MemberTopicRepliesPage;
import com.hjx.v2ex.bean.MemberTopicsPage;
import com.hjx.v2ex.bean.Reply;
import com.hjx.v2ex.flexibleitem.SimpleFlexibleHeaderItem;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.flexibleitem.TopicFlexibleItem;
import com.hjx.v2ex.flexibleitem.ViewMoreFlexibleItem;
import com.hjx.v2ex.network.RetrofitSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberDetailsFragment extends DataLoadingBaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    private String memberName;
    private FlexibleAdapter memberDetailsAdapter;
    private Member member;
    private List<Topic> topics;
    private List<Map<Reply, Topic>> replies;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static MemberDetailsFragment newInstance(String memberName) {
        MemberDetailsFragment memberDetailsFragment = new MemberDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_MEMBER, memberName);
        memberDetailsFragment.setArguments(bundle);
        return memberDetailsFragment;
    }

    @Override
    public int getContentRes() {
        return R.layout.recycler_view_layout;
    }

    @Override
    protected void initView() {
        memberName = getArguments().getString(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_MEMBER);
        swipeRefreshLayout.setOnRefreshListener(this);
        memberDetailsAdapter = new FlexibleAdapter(new ArrayList());
        memberDetailsAdapter.setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true);
        recyclerView.setAdapter(memberDetailsAdapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext()));
    }

    @Override
    protected void loadData() {
        loadMemberDetails();
        loadMemberTopics();
        loadMemberReplies();
    }

    @Override
    public void onRefresh() {
        member = null;
        topics = null;
        replies = null;
        loadData();
    }

    private void loadMemberDetails() {
        RetrofitSingleton.getInstance().memberDetailsPage(memberName).enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                swipeRefreshLayout.setRefreshing(false);
                member = response.body();
                if(member != null) {
                    memberDetailsAdapter.clear();
                    memberDetailsAdapter.addScrollableHeader(new MemberDetailsFlexibleItem(member));
                    if(topics != null) {
                        showMemberTopics();
                    }
                    if(replies != null) {
                        showMemberReplies();
                    }
                    successLoadingData();
                } else {
                    errorLoadingData();
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                errorLoadingData();
                throwable.printStackTrace();
            }
        });
    }

    private void loadMemberTopics() {
        RetrofitSingleton.getInstance().memberTopicsPage(memberName, 1).enqueue(new Callback<MemberTopicsPage>() {
            @Override
            public void onResponse(Call<MemberTopicsPage> call, Response<MemberTopicsPage> response) {
                topics = response.body().getTopics().getCurrentPageItems();
                if(member != null) {
                    showMemberTopics();
                }
            }

            @Override
            public void onFailure(Call<MemberTopicsPage> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void showMemberTopics() {
        try {
            List<AbstractFlexibleItem> topicFlexibleItems = new ArrayList<>();
            SimpleFlexibleHeaderItem headerItem = new SimpleFlexibleHeaderItem(memberName + "发表的主题");
            int i = 0;
            for(Topic topic : topics) {
                if(++i > 5) {
                    break;
                }
                topicFlexibleItems.add(new TopicFlexibleItem(topic, TopicFlexibleItem.TopicItemType.MEMBER, headerItem));
            }
            if(topics.size() > 5) {
                topicFlexibleItems.add(new ViewMoreFlexibleItem(memberName, ViewMoreFlexibleItem.ViewMoreType.TOPIC));
            }
            memberDetailsAdapter.addItems(1, topicFlexibleItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMemberReplies() {
        RetrofitSingleton.getInstance().memberTopicRepliesPage(memberName, 1).enqueue(new Callback<MemberTopicRepliesPage>() {
            @Override
            public void onResponse(Call<MemberTopicRepliesPage> call, Response<MemberTopicRepliesPage> response) {
                replies = response.body().getReplies().getCurrentPageItems();
                if(member != null) {
                    showMemberReplies();
                }
            }

            @Override
            public void onFailure(Call<MemberTopicRepliesPage> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void showMemberReplies() {
        try {
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
                replyFlexibleItems.add(new ViewMoreFlexibleItem(memberName, ViewMoreFlexibleItem.ViewMoreType.REPLY));
            }
            memberDetailsAdapter.addItems(memberDetailsAdapter.getItemCount(), replyFlexibleItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
