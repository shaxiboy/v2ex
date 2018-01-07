package com.hjx.v2ex.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.Member;
import com.hjx.v2ex.bean.MemberTopicReplies;
import com.hjx.v2ex.bean.MemberFavoriteResult;
import com.hjx.v2ex.bean.Reply;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.bean.TopicsPageData;
import com.hjx.v2ex.flexibleitem.MemberDetailsFlexibleItem;
import com.hjx.v2ex.flexibleitem.MemberReplyFlexibleItem;
import com.hjx.v2ex.flexibleitem.SimpleFlexibleHeaderItem;
import com.hjx.v2ex.flexibleitem.TopicFlexibleItem;
import com.hjx.v2ex.flexibleitem.ViewMoreFlexibleItem;
import com.hjx.v2ex.network.RetrofitService;
import com.hjx.v2ex.network.RetrofitServiceSingleton;
import com.hjx.v2ex.util.V2EXUtil;

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
public class MemberDetailsFragment extends DataLoadingBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

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
        bundle.putString(DataLoadingBaseActivity.ARG_MEMBERNAME, memberName);
        memberDetailsFragment.setArguments(bundle);
        return memberDetailsFragment;
    }

    @Override
    public int getContentRes() {
        return R.layout.recycler_view_layout;
    }

    @Override
    protected void initView() {
        memberName = getArguments().getString(DataLoadingBaseActivity.ARG_MEMBERNAME);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem favoriteItem = menu.findItem(R.id.menu_favorite);
        if (V2EXUtil.isLogin(getContext()) && !memberDetailsAdapter.isEmpty()) {
            String favoriteURL = ((MemberDetailsFlexibleItem) memberDetailsAdapter.getScrollableHeaders().get(0)).getMember().getFavoriteURL();
            FavoriteMemberType type = getFavoriteMemberType(favoriteURL);
            if (type != null) {
                if (type == FavoriteMemberType.UNFAVORITE) {
                    favoriteItem.setIcon(R.drawable.favorite_red);
                } else if (type == FavoriteMemberType.FAVORITE) {
                    favoriteItem.setIcon(R.drawable.favorite_white);
                }
                favoriteItem.setVisible(true);
            } else {
                favoriteItem.setVisible(false);
            }
        } else {
            favoriteItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite:
                favoriteMember();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void favoriteMember() {
        String favoriteURL = ((MemberDetailsFlexibleItem) memberDetailsAdapter.getScrollableHeaders().get(0)).getMember().getFavoriteURL();
        final FavoriteMemberType type = getFavoriteMemberType(favoriteURL);
        if (type != null) {
            String referer = RetrofitService.BASE_URL + "member/" + memberName;
            final ProgressDialog progressDialog = V2EXUtil.showProgressDialog(getContext(), "正在" + type + memberName);
            RetrofitServiceSingleton.getInstance(getActivity().getApplication()).favoriteMember(favoriteURL, referer).enqueue(new Callback<MemberFavoriteResult>() {
                @Override
                public void onResponse(Call<MemberFavoriteResult> call, Response<MemberFavoriteResult> response) {
                    progressDialog.dismiss();
                    MemberFavoriteResult result = response.body();
                    boolean success = false;
                    if (result != null) {
                        FavoriteMemberType newType = getFavoriteMemberType(result.getFavoriteURL());
                        if (newType != null) {
                            if (type == FavoriteMemberType.UNFAVORITE && newType == FavoriteMemberType.FAVORITE
                                    || type == FavoriteMemberType.FAVORITE && newType == FavoriteMemberType.UNFAVORITE) {
                                success = true;
                            }
                        }
                    }
                    if (success) {
                        Toast.makeText(MemberDetailsFragment.this.getContext(), type + memberName + "操作成功", Toast.LENGTH_SHORT).show();
                        ((MemberDetailsFlexibleItem) memberDetailsAdapter.getScrollableHeaders().get(0)).getMember().setFavoriteURL(result.getFavoriteURL());
                        getActivity().invalidateOptionsMenu();
                    } else {
                        Toast.makeText(MemberDetailsFragment.this.getContext(), type + memberName + "操作失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MemberFavoriteResult> call, Throwable throwable) {
                    progressDialog.dismiss();
                    Toast.makeText(MemberDetailsFragment.this.getContext(), type + memberName + "操作失败", Toast.LENGTH_SHORT).show();
                    throwable.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        member = null;
        topics = null;
        replies = null;
        loadData();
    }

    private void loadMemberDetails() {
        RetrofitServiceSingleton.getInstance(getActivity().getApplication()).getMember(memberName).enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                try {
                    swipeRefreshLayout.setRefreshing(false);
                    member = response.body();
                    memberDetailsAdapter.clear();
                    if (member != null) {
                        memberDetailsAdapter.addScrollableHeader(new MemberDetailsFlexibleItem(member));
                        if (topics != null) {
                            showMemberTopics();
                        }
                        if (replies != null) {
                            showMemberReplies();
                        }
                        successLoadingData();
                    } else {
                        errorLoadingData();
                    }
                    getActivity().invalidateOptionsMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable throwable) {
                try {
                    swipeRefreshLayout.setRefreshing(false);
                    memberDetailsAdapter.clear();
                    errorLoadingData();
                    getActivity().invalidateOptionsMenu();
                    throwable.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadMemberTopics() {
        RetrofitServiceSingleton.getInstance(getActivity().getApplication()).getMemberTopics(memberName, 1).enqueue(new Callback<TopicsPageData>() {
            @Override
            public void onResponse(Call<TopicsPageData> call, Response<TopicsPageData> response) {
                TopicsPageData topicsPageData = response.body();
                if(topicsPageData != null) {
                    topics = topicsPageData.getTopics().getCurrentPageItems();
                    if (member != null) {
                        showMemberTopics();
                    }
                }
            }

            @Override
            public void onFailure(Call<TopicsPageData> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void showMemberTopics() {
        try {
            List<AbstractFlexibleItem> topicFlexibleItems = new ArrayList<>();
            SimpleFlexibleHeaderItem headerItem = new SimpleFlexibleHeaderItem(memberName + "发表的主题");
            if (!topics.isEmpty()) {
                topicFlexibleItems.add(headerItem);
            }
            int i = 0;
            for (Topic topic : topics) {
                if (++i > 5) {
                    break;
                }
                topicFlexibleItems.add(new TopicFlexibleItem(topic));
            }
            if (topics.size() > 5) {
                topicFlexibleItems.add(new ViewMoreFlexibleItem(memberName, ViewMoreFlexibleItem.ViewMoreType.TOPIC));
            }
            memberDetailsAdapter.addItems(1, topicFlexibleItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMemberReplies() {
        RetrofitServiceSingleton.getInstance(getActivity().getApplication()).getMemberTopicReplies(memberName, 1).enqueue(new Callback<MemberTopicReplies>() {
            @Override
            public void onResponse(Call<MemberTopicReplies> call, Response<MemberTopicReplies> response) {
                MemberTopicReplies memberTopicReplies = response.body();
                if (memberTopicReplies != null) {
                    replies = memberTopicReplies.getReplies().getCurrentPageItems();
                    if (member != null) {
                        showMemberReplies();
                    }
                }
            }

            @Override
            public void onFailure(Call<MemberTopicReplies> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void showMemberReplies() {
        try {
            List<AbstractFlexibleItem> replyFlexibleItems = new ArrayList<>();
            SimpleFlexibleHeaderItem headerItem = new SimpleFlexibleHeaderItem(memberName + "发表的回复");
            int i = 0;
            for (Map<Reply, Topic> replyMap : replies) {
                if (++i > 5) {
                    break;
                }
                replyFlexibleItems.add(new MemberReplyFlexibleItem(replyMap, headerItem));
            }
            if (replies.size() > 5) {
                replyFlexibleItems.add(new ViewMoreFlexibleItem(memberName, ViewMoreFlexibleItem.ViewMoreType.REPLY));
            }
            memberDetailsAdapter.addItems(memberDetailsAdapter.getItemCount(), replyFlexibleItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FavoriteMemberType getFavoriteMemberType(String url) {
        if (url != null) {
            if (url.contains("unfollow")) return FavoriteMemberType.UNFAVORITE;
            if (url.contains("follow")) return FavoriteMemberType.FAVORITE;
        }
        return null;
    }

    public enum FavoriteMemberType {

        FAVORITE("关注"), UNFAVORITE("取消关注");

        private String action;

        FavoriteMemberType(String action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }
    }
}
