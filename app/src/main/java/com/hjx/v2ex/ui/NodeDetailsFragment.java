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
import com.hjx.v2ex.bean.NodeFavoriteResult;
import com.hjx.v2ex.bean.NodePage;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.bean.TopicsPageData;
import com.hjx.v2ex.flexibleitem.NodeDetailsFlexibleItem;
import com.hjx.v2ex.flexibleitem.ProgressItem;
import com.hjx.v2ex.flexibleitem.TopicFlexibleItem;
import com.hjx.v2ex.network.RetrofitService;
import com.hjx.v2ex.network.RetrofitSingleton;
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
public class NodeDetailsFragment extends DataLoadingBaseFragment implements SwipeRefreshLayout.OnRefreshListener, FlexibleAdapter.EndlessScrollListener {

    private String nodeName;
    private FlexibleAdapter nodeDetailsAdapter;
    private int currentPage = 2;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static NodeDetailsFragment newInstance(String nodeName) {
        NodeDetailsFragment topicDetailsFragment = new NodeDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DataLoadingBaseActivity.ARG_NODENAME, nodeName);
        topicDetailsFragment.setArguments(bundle);
        return topicDetailsFragment;
    }

    @Override
    public int getContentRes() {
        return R.layout.recycler_view_layout;
    }

    @Override
    protected void initView() {
        nodeName = getArguments().getString(DataLoadingBaseActivity.ARG_NODENAME);
        swipeRefreshLayout.setOnRefreshListener(this);
        nodeDetailsAdapter = new FlexibleAdapter(new ArrayList());
        recyclerView.setAdapter(nodeDetailsAdapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext()));
    }

    @Override
    protected void loadData() {
        loadNodePage();
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
        if (V2EXUtil.isLogin(getContext()) && !nodeDetailsAdapter.isEmpty()) {
            String favoriteURL = ((NodeDetailsFlexibleItem) nodeDetailsAdapter.getScrollableHeaders().get(0)).getNode().getFavoriteURL();
            FavoriteNodeType type = getFavoriteNodeType(favoriteURL);
            if (type != null) {
                if (type == FavoriteNodeType.UNFAVORITE) {
                    favoriteItem.setIcon(R.drawable.ic_menu_favorite);
                } else if (type == FavoriteNodeType.FAVORITE) {
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
                favoriteNode();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void favoriteNode() {
        String favoriteURL = ((NodeDetailsFlexibleItem) nodeDetailsAdapter.getScrollableHeaders().get(0)).getNode().getFavoriteURL();
        final FavoriteNodeType type = getFavoriteNodeType(favoriteURL);
        if (type != null) {
            String referer = RetrofitService.BASE_URL + "go/" + nodeName;
            final ProgressDialog progressDialog = V2EXUtil.showProgressDialog(getContext(), "正在" + type);
            RetrofitSingleton.getInstance(getContext()).favoriteNode(favoriteURL, referer).enqueue(new Callback<NodeFavoriteResult>() {
                @Override
                public void onResponse(Call<NodeFavoriteResult> call, Response<NodeFavoriteResult> response) {
                    progressDialog.dismiss();
                    NodeFavoriteResult result = response.body();
                    boolean success = false;
                    if (result != null) {
                        FavoriteNodeType newType = getFavoriteNodeType(result.getFavoriteURL());
                        if (newType != null) {
                            if (type == FavoriteNodeType.UNFAVORITE && newType == FavoriteNodeType.FAVORITE
                                    || type == FavoriteNodeType.FAVORITE && newType == FavoriteNodeType.UNFAVORITE) {
                                success = true;
                            }
                        }
                    }
                    if (success) {
                        Toast.makeText(NodeDetailsFragment.this.getContext(), type + "操作成功", Toast.LENGTH_SHORT).show();
                        ((NodeDetailsFlexibleItem) nodeDetailsAdapter.getScrollableHeaders().get(0)).getNode().setFavoriteURL(result.getFavoriteURL());
                        getActivity().invalidateOptionsMenu();
                    } else {
                        Toast.makeText(NodeDetailsFragment.this.getContext(), type + "操作失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<NodeFavoriteResult> call, Throwable throwable) {
                    progressDialog.dismiss();
                    Toast.makeText(NodeDetailsFragment.this.getContext(), type + "操作失败", Toast.LENGTH_SHORT).show();
                    throwable.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        currentPage = 2;
        loadNodePage();
    }


    private void loadNodePage() {
        RetrofitSingleton.getInstance(getContext()).getNodePage(nodeName).enqueue(new Callback<NodePage>() {
            @Override
            public void onResponse(Call<NodePage> call, Response<NodePage> response) {
                swipeRefreshLayout.setRefreshing(false);
                NodePage nodePage = response.body();
                if (nodePage != null) {
                    List<TopicFlexibleItem> topics = new ArrayList<>();
                    for (Topic topic : nodePage.getTopics().getCurrentPageItems()) {
                        topics.add(new TopicFlexibleItem(topic));
                    }
                    nodeDetailsAdapter.clear();
                    if (nodePage.getNode() != null) {
                        successLoadingData();
                        nodeDetailsAdapter.addScrollableHeader(new NodeDetailsFlexibleItem(nodePage.getNode()));
                        nodeDetailsAdapter.notifyDataSetChanged();
                        nodeDetailsAdapter.addItems(nodeDetailsAdapter.getItemCount(), topics);
                        if (nodePage.getTopics().getTotalPage() >= 2) {
                            nodeDetailsAdapter.setEndlessScrollListener(NodeDetailsFragment.this, new ProgressItem())
                                    .setEndlessTargetCount(nodePage.getTopics().getTotalItems());
                        }
                    } else {
                        errorLoadingData();
                        return;
                    }
                } else {
                    nodeDetailsAdapter.clear();
                    errorLoadingData();
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onFailure(Call<NodePage> call, Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                nodeDetailsAdapter.clear();
                errorLoadingData();
                getActivity().invalidateOptionsMenu();
                throwable.printStackTrace();
            }
        });
    }

    private void loadNodeTopics() {
        RetrofitSingleton.getInstance(getContext()).getNodeTopics(nodeName, currentPage).enqueue(new Callback<TopicsPageData>() {
            @Override
            public void onResponse(Call<TopicsPageData> call, Response<TopicsPageData> response) {
                TopicsPageData topicsPageData = response.body();
                if (topicsPageData != null) {
                    List<TopicFlexibleItem> topics = new ArrayList<>();
                    for (Topic topic : topicsPageData.getTopics().getCurrentPageItems()) {
                        topics.add(new TopicFlexibleItem(topic));
                    }
                    nodeDetailsAdapter.onLoadMoreComplete(topics, 5000);
                    if (!topicsPageData.getTopics().getCurrentPageItems().isEmpty()) {
                        currentPage++;
                    }
                }
            }

            @Override
            public void onFailure(Call<TopicsPageData> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void noMoreLoad(int newItemsSize) {

    }

    @Override
    public void onLoadMore(int lastPosition, int currentPage) {
        loadNodeTopics();
    }

    public FavoriteNodeType getFavoriteNodeType(String url) {
        if (url != null) {
            if (url.contains("unfavorite")) return FavoriteNodeType.UNFAVORITE;
            if (url.contains("favorite")) return FavoriteNodeType.FAVORITE;
        }
        return null;
    }

    public enum FavoriteNodeType {

        FAVORITE("收藏节点"), UNFAVORITE("取消收藏节点");

        private String action;

        FavoriteNodeType(String action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }
    }
}
