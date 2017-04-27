package com.hjx.v2ex.ui;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.PageData;
import com.hjx.v2ex.flexibleitem.ProgressItem;

import java.util.ArrayList;
import java.util.List;

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
public abstract class ListBaseFragment extends DataLoadingBaseFragment implements SwipeRefreshLayout.OnRefreshListener, FlexibleAdapter.EndlessScrollListener {

    private FlexibleAdapter listAdapter;
    private int currentPage = 1;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public int getCurrentPage() {
        return currentPage;
    }

    public FlexibleAdapter getListAdapter() {
        return listAdapter;
    }

    @Override
    public int getContentRes() {
        return R.layout.recycler_view_layout;
    }

    @Override
    protected void initView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        listAdapter = new FlexibleAdapter(new ArrayList());
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(getLayoutManager());
    }

    RecyclerView.LayoutManager getLayoutManager() {
        return new SmoothScrollLinearLayoutManager(getContext());
    }

    public Callback getListBaseFragmentCallBack() {
        return new Callback<ListBaseFragmentData>() {
            @Override
            public void onResponse(Call<ListBaseFragmentData> call, Response<ListBaseFragmentData> response) {
                swipeRefreshLayout.setRefreshing(false);
                ListBaseFragmentData data = response.body();
                if (data != null) {
                    List<AbstractFlexibleItem> items = new ArrayList<>();
                    for(Object item : data.getPageData().getCurrentPageItems()) {
                        items.add(getFlexibleItem(item));
                    }
                    if (currentPage == 1) {
                        listAdapter.clear();
                        if (data.getPageData() != null) {
                            successLoadingData();
                            listAdapter.addItems(0, items);
                            if (data.getPageData().getTotalPage() >= 2) {
                                listAdapter.setEndlessScrollListener(ListBaseFragment.this, new ProgressItem())
                                        .setEndlessTargetCount(data.getPageData().getTotalItems());
                            }
                        } else {
                            errorLoadingData();
                            return;
                        }

                    } else {
                        listAdapter.onLoadMoreComplete(items, 5000);
                    }
                    if (!data.getPageData().getCurrentPageItems().isEmpty()) {
                        currentPage++;
                    }
                } else {
                    if (currentPage == 1) {
                        listAdapter.clear();
                        errorLoadingData();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListBaseFragmentData> call, Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                if (currentPage == 1) {
                    listAdapter.clear();
                    errorLoadingData();
                }
                throwable.printStackTrace();
            }
        };
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void noMoreLoad(int newItemsSize) {

    }

    @Override
    public void onLoadMore(int lastPosition, int currentPage) {
        loadData();
    }

    abstract AbstractFlexibleItem getFlexibleItem(Object item);

    public interface ListBaseFragmentData {
        PageData getPageData();
    }
}
