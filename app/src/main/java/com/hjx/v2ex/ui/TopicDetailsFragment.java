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
import com.hjx.v2ex.bean.TopicFavoriteResult;
import com.hjx.v2ex.flexibleitem.ProgressItem;
import com.hjx.v2ex.bean.Reply;
import com.hjx.v2ex.flexibleitem.TopicDetailsFlexibleItem;
import com.hjx.v2ex.bean.TopicPage;
import com.hjx.v2ex.flexibleitem.TopicReplyFlexibleItem;
import com.hjx.v2ex.network.RetrofitService;
import com.hjx.v2ex.network.RetrofitSingleton;
import com.hjx.v2ex.util.LogUtil;
import com.hjx.v2ex.util.V2EXUtil;

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
public class TopicDetailsFragment extends DataLoadingBaseFragment implements SwipeRefreshLayout.OnRefreshListener, FlexibleAdapter.EndlessScrollListener {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private int topicId;
    private FlexibleAdapter topicDetailsAdapter;
    private int currentPage = 1;

    public static TopicDetailsFragment newInstance(String topicId) {
        TopicDetailsFragment topicDetailsFragment = new TopicDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DataLoadingBaseActivity.ARG_TOPICID, Integer.parseInt(topicId));
        topicDetailsFragment.setArguments(bundle);
        return topicDetailsFragment;
    }

    @Override
    public int getContentRes() {
        return R.layout.recycler_view_layout;
    }

    @Override
    protected void initView() {
        topicId = getArguments().getInt(DataLoadingBaseActivity.ARG_TOPICID);
        swipeRefreshLayout.setOnRefreshListener(this);
        topicDetailsAdapter = new FlexibleAdapter(new ArrayList());
        recyclerView.setAdapter(topicDetailsAdapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext()));
    }

    @Override
    protected void loadData() {
        loadTopicDetails();
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
        if(V2EXUtil.isLogin(getContext()) && !topicDetailsAdapter.isEmpty()) {
            String favoriteURL = ((TopicDetailsFlexibleItem) topicDetailsAdapter.getScrollableHeaders().get(0)).getTopic().getFavoriteURL();
            FavoriteTopicType type = getFavoriteTopicType(favoriteURL);
            if(type != null) {
                if(type == FavoriteTopicType.UNFAVORITE) {
                    favoriteItem.setIcon(R.drawable.ic_menu_favorite);
                } else if(type == FavoriteTopicType.FAVORITE){
                    favoriteItem.setIcon(R.drawable.ic_menu_unfavorite);
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
                favoriteTopic();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        loadTopicDetails();
    }

    private void favoriteTopic() {
        String favoriteURL = ((TopicDetailsFlexibleItem) topicDetailsAdapter.getScrollableHeaders().get(0)).getTopic().getFavoriteURL();
        final FavoriteTopicType type = getFavoriteTopicType(favoriteURL);
        if(type != null) {
            String referer = RetrofitService.BASE_URL + "t/" + topicId;
            final ProgressDialog progressDialog = V2EXUtil.showProgressDialog(getContext(), "正在" + type);
            RetrofitSingleton.getInstance(getContext()).favoriteTopic(favoriteURL, referer).enqueue(new Callback<TopicFavoriteResult>() {
                @Override
                public void onResponse(Call<TopicFavoriteResult> call, Response<TopicFavoriteResult> response) {
                    progressDialog.dismiss();
                    TopicFavoriteResult result = response.body();
                    boolean success = false;
                    if(result != null) {
                        FavoriteTopicType newType = getFavoriteTopicType(result.getFavoriteURL());
                        if(newType != null) {
                            if(type == FavoriteTopicType.UNFAVORITE && newType == FavoriteTopicType.FAVORITE
                                    || type == FavoriteTopicType.FAVORITE && newType == FavoriteTopicType.UNFAVORITE) {
                                success = true;
                            }
                        }
                    }
                    if(success) {
                        Toast.makeText(TopicDetailsFragment.this.getContext(), type + "操作成功", Toast.LENGTH_SHORT).show();
                        ((TopicDetailsFlexibleItem) topicDetailsAdapter.getScrollableHeaders().get(0)).getTopic().setFavoriteURL(result.getFavoriteURL());
                        getActivity().invalidateOptionsMenu();
                    } else {
                        Toast.makeText(TopicDetailsFragment.this.getContext(), type + "操作失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TopicFavoriteResult> call, Throwable throwable) {
                    progressDialog.dismiss();
                    Toast.makeText(TopicDetailsFragment.this.getContext(), type + "操作失败", Toast.LENGTH_SHORT).show();
                    throwable.printStackTrace();
                }
            });
        }
    }

    private void loadTopicDetails() {
        RetrofitSingleton.getInstance(getContext()).getTopicPage(topicId, currentPage).enqueue(new Callback<TopicPage>() {
            @Override
            public void onResponse(Call<TopicPage> call, Response<TopicPage> response) {
                swipeRefreshLayout.setRefreshing(false);
                TopicPage topicPage = response.body();
                if (topicPage != null) {
                    LogUtil.d(topicPage.toString());
                    List<TopicReplyFlexibleItem> topicReplies = new ArrayList<>();
                    for (Reply reply : topicPage.getReplies().getCurrentPageItems()) {
                        topicReplies.add(new TopicReplyFlexibleItem(reply));
                    }
                    if (currentPage == 1) {
                        topicDetailsAdapter.clear();
                        if (topicPage.getTopic() != null) {
                            successLoadingData();
                            topicDetailsAdapter.addScrollableHeader(new TopicDetailsFlexibleItem(topicPage.getTopic()));
                            topicDetailsAdapter.notifyDataSetChanged();
                            topicDetailsAdapter.addItems(topicDetailsAdapter.getItemCount(), topicReplies);
                            if (topicPage.getReplies().getTotalPage() >= 2) {
                                topicDetailsAdapter.setEndlessScrollListener(TopicDetailsFragment.this, new ProgressItem())
                                        .setEndlessTargetCount(topicPage.getReplies().getTotalItems());
                            }
                        } else {
                            errorLoadingData();
                            return;
                        }
                    } else {
                        topicDetailsAdapter.onLoadMoreComplete(topicReplies, 5000);
                    }
                    if (!topicPage.getReplies().getCurrentPageItems().isEmpty()) {
                        currentPage++;
                    }
                } else {
                    if (currentPage == 1) {
                        topicDetailsAdapter.clear();
                        errorLoadingData();
                    }
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onFailure(Call<TopicPage> call, Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                if (currentPage == 1) {
                    topicDetailsAdapter.clear();
                    errorLoadingData();
                }
                getActivity().invalidateOptionsMenu();
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void noMoreLoad(int newItemsSize) {

    }

    @Override
    public void onLoadMore(int lastPosition, int currentPage) {
        loadTopicDetails();
    }

    public FavoriteTopicType getFavoriteTopicType(String url) {
        if(url != null) {
            if(url.contains("unfavorite")) return FavoriteTopicType.UNFAVORITE;
            if(url.contains("favorite")) return FavoriteTopicType.FAVORITE;
        }
        return null;
    }

    public enum FavoriteTopicType {

        FAVORITE("收藏主题"), UNFAVORITE("取消收藏主题");

        private String action;

        FavoriteTopicType(String action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }
    }

}
