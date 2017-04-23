package com.hjx.v2ex.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.flexibleitem.MemberReplyFlexibleItem;
import com.hjx.v2ex.bean.MemberTopicRepliesPage;
import com.hjx.v2ex.flexibleitem.ProgressItem;
import com.hjx.v2ex.bean.Reply;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.network.RetrofitSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberRepliesFragment extends DataLoadingBaseFragment implements SwipeRefreshLayout.OnRefreshListener, FlexibleAdapter.EndlessScrollListener {

    private String memberName;
    private FlexibleAdapter memberRepliesAdapter;
    private int currentPage = 1;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static MemberRepliesFragment newInstance(String memberName) {
        MemberRepliesFragment memberRepliesFragment = new MemberRepliesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_MEMBER, memberName);
        memberRepliesFragment.setArguments(bundle);
        return memberRepliesFragment;
    }

    @Override
    public int getContentRes() {
        return R.layout.recycler_view_layout;
    }

    @Override
    protected void initView() {
        memberName = getArguments().getString(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_MEMBER);
        swipeRefreshLayout.setOnRefreshListener(this);
        memberRepliesAdapter = new FlexibleAdapter(new ArrayList());
        recyclerView.setAdapter(memberRepliesAdapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext()));
    }

    @Override
    protected void loadData() {
        loadMemberReplies();
    }

    @Override
    public void onRefresh() {
        loadMemberReplies();
    }

    private void loadMemberReplies() {
        RetrofitSingleton.getInstance().memberTopicRepliesPage(memberName, currentPage).enqueue(new Callback<MemberTopicRepliesPage>() {
            @Override
            public void onResponse(Call<MemberTopicRepliesPage> call, Response<MemberTopicRepliesPage> response) {
                swipeRefreshLayout.setRefreshing(false);
                MemberTopicRepliesPage memberRepliesPage = response.body();
                if (memberRepliesPage != null) {
                    List<MemberReplyFlexibleItem> memberReplies = new ArrayList<>();
                    for(Map<Reply, Topic> replyMap : memberRepliesPage.getReplies().getCurrentPageItems()) {
                        memberReplies.add(new MemberReplyFlexibleItem(replyMap, null));
                    }
                    if (currentPage == 1) {
                        if (memberRepliesPage.getReplies() != null) {
                            successLoadingData();
                            memberRepliesAdapter.clear();
                            memberRepliesAdapter.addItems(0, memberReplies);
                            if (memberRepliesPage.getReplies().getTotalPage() >= 2) {
                                memberRepliesAdapter.setEndlessScrollListener(MemberRepliesFragment.this, new ProgressItem())
                                        .setEndlessTargetCount(memberRepliesPage.getReplies().getTotalItems());
                            }
                        } else {
                            errorLoadingData();
                            return;
                        }

                    } else {
                        memberRepliesAdapter.onLoadMoreComplete(memberReplies, 5000);
                    }
                    if (!memberRepliesPage.getReplies().getCurrentPageItems().isEmpty()) {
                        currentPage++;
                    }
                } else {
                    if (currentPage == 1) {
                        errorLoadingData();
                    }
                }
            }

            @Override
            public void onFailure(Call<MemberTopicRepliesPage> call, Throwable throwable) {
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
        loadMemberReplies();
    }
}
