package com.hjx.v2ex.ui;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.PageData;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.bean.TopicsPageData;
import com.hjx.v2ex.flexibleitem.ProgressItem;
import com.hjx.v2ex.flexibleitem.TopicFlexibleItem;

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
public abstract class ListBaseFragment<T> extends DataLoadingBaseFragment implements SwipeRefreshLayout.OnRefreshListener, FlexibleAdapter.EndlessScrollListener {

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
        listAdapter.setDisplayHeadersAtStartUp(true)
                .expandItemsAtStartUp()
                .setStickyHeaders(true);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(getLayoutManager());
    }

    RecyclerView.LayoutManager getLayoutManager() {
        return new SmoothScrollLinearLayoutManager(getContext());
    }

    public Callback getListBaseFragmentCallBack() {
        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                swipeRefreshLayout.setRefreshing(false);
                T data = response.body();
                if (data != null) {
                    PageData pageData = getPageData(data);
                    if (currentPage == 1) {
                        if (pageData.getTotalItems() != 0) {
                            successLoadingData();
                            listAdapter.updateDataSet(pageData.getCurrentPageItems());
                            if (pageData.getTotalPage() >= 2) {
                                listAdapter.setEndlessScrollListener(ListBaseFragment.this, new ProgressItem())
                                        .setEndlessTargetCount(pageData.getTotalItems())
                                        .setEndlessPageSize(pageData.getTotalPage());
                            }
                        }
                    } else {
                        if (pageData.getTotalItems() != 0) {
                            listAdapter.onLoadMoreComplete(pageData.getCurrentPageItems(), 5000);
                            currentPage++;
                        }
                    }
                } else {
                    if (currentPage == 1) {
                        listAdapter.clear();
                        errorLoadingData();
                    }
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
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

    abstract PageData<AbstractFlexibleItem> getPageData(T data);

    public PageData<AbstractFlexibleItem> getOnePageData(List items) {
        PageData<AbstractFlexibleItem> pageData = new PageData<>();
        if(items != null && !items.isEmpty()) {
            pageData.setCurrentPage(1);
            pageData.setTotalPage(1);
            pageData.setTotalItems(items.size());
            pageData.setCurrentPageItems(items);
        }
        return pageData;
    }

    public PageData<AbstractFlexibleItem> getFlexibleTopicPageData(TopicsPageData data) {
        PageData<AbstractFlexibleItem> pageData = new PageData<>();
        copyPageDataStatistics(data.getTopics(), pageData);
        for(Topic topic : data.getTopics().getCurrentPageItems()) {
            pageData.getCurrentPageItems().add(new TopicFlexibleItem(topic));
        }
        return pageData;
    }

    public static void copyPageDataStatistics(PageData source, PageData target) {
        target.setCurrentPage(source.getCurrentPage());
        target.setTotalPage(source.getTotalPage());
        target.setTotalItems(source.getTotalItems());
    }
}
