package com.hjx.v2ex.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.NodeFavoriteResult;
import com.hjx.v2ex.bean.NodePage;
import com.hjx.v2ex.bean.PageData;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.flexibleitem.NodeDetailsFlexibleItem;
import com.hjx.v2ex.flexibleitem.TopicFlexibleItem;
import com.hjx.v2ex.network.RetrofitService;
import com.hjx.v2ex.network.RetrofitServiceSingleton;
import com.hjx.v2ex.util.V2EXUtil;

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NodeDetailsFragment extends ListBaseFragment<NodePage> {

    private String nodeName;

    public static NodeDetailsFragment newInstance(String nodeName) {
        NodeDetailsFragment topicDetailsFragment = new NodeDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DataLoadingBaseActivity.ARG_NODENAME, nodeName);
        topicDetailsFragment.setArguments(bundle);
        return topicDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nodeName = getArguments().getString(DataLoadingBaseActivity.ARG_NODENAME);
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
        if (V2EXUtil.isLogin(getContext()) && !getListAdapter().isEmpty()) {
            String favoriteURL = ((NodeDetailsFlexibleItem) getListAdapter().getScrollableHeaders().get(0)).getNode().getFavoriteURL();
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

    @Override
    ListData getListData(NodePage data) {
        ListData listData = new ListData();
        listData.setHeader(new NodeDetailsFlexibleItem(data.getNode()));
        PageData<AbstractFlexibleItem> pageData = new PageData<>();
        copyPageDataStatistics(data.getTopics(), pageData);
        for(Topic topic : data.getTopics().getCurrentPageItems()) {
            pageData.getCurrentPageItems().add(new TopicFlexibleItem(topic));
        }
        listData.setPageData(pageData);
        return listData;
    }


    private void loadNodePage() {
        RetrofitServiceSingleton.getInstance(getContext()).getNodePage(nodeName, getCurrentPage()).enqueue(getListBaseFragmentCallBack());
    }

    private void favoriteNode() {
        String favoriteURL = ((NodeDetailsFlexibleItem) getListAdapter().getScrollableHeaders().get(0)).getNode().getFavoriteURL();
        final FavoriteNodeType type = getFavoriteNodeType(favoriteURL);
        if (type != null) {
            String referer = RetrofitService.BASE_URL + "go/" + nodeName;
            final ProgressDialog progressDialog = V2EXUtil.showProgressDialog(getContext(), "正在" + type);
            RetrofitServiceSingleton.getInstance(getContext()).favoriteNode(favoriteURL, referer).enqueue(new Callback<NodeFavoriteResult>() {
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
                        ((NodeDetailsFlexibleItem) getListAdapter().getScrollableHeaders().get(0)).getNode().setFavoriteURL(result.getFavoriteURL());
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
