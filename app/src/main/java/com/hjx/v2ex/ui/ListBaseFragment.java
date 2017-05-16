package com.hjx.v2ex.ui;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
                try {
                    swipeRefreshLayout.setRefreshing(false);
                    T data = response.body();
                    if (data != null) {
                        ListData listData = getListData(data);
                        if (currentPage == 1) {
                            listAdapter.clear();
                            successLoadingData();
                            if(listData.getHeader() != null) {
                                listAdapter.addScrollableHeader(listData.getHeader());
                                listAdapter.notifyDataSetChanged();
                            }
                            if (listData.getPageData().getTotalItems() != 0) {
                                listAdapter.updateDataSet(listData.getPageData().getCurrentPageItems());
                                if (listData.getPageData().getTotalPage() >= 2) {
                                    listAdapter.setEndlessScrollListener(ListBaseFragment.this, new ProgressItem())
                                            .setEndlessTargetCount(listData.getPageData().getTotalItems());
                                }
                            }
                        } else {
                            if (!listData.getPageData().getCurrentPageItems().isEmpty()) {
                                listAdapter.onLoadMoreComplete(listData.getPageData().getCurrentPageItems(), 5000);
                            }
                        }
                        if (!listData.getPageData().getCurrentPageItems().isEmpty()) {
                            currentPage++;
                        }
                    } else {
                        if (currentPage == 1) {
                            listAdapter.clear();
                            errorLoadingData();
                        }
                    }
                    getActivity().invalidateOptionsMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
                try {
                    swipeRefreshLayout.setRefreshing(false);
                    if (currentPage == 1) {
                        listAdapter.clear();
                        errorLoadingData();
                    }
                    getActivity().invalidateOptionsMenu();
                    throwable.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        loadData();
    }

    @Override
    public void noMoreLoad(int newItemsSize) {

    }

    @Override
    public void onLoadMore(int lastPosition, int currentPage) {
        loadData();
    }

    abstract ListData getListData(T data);

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

    public static void copyPageDataStatistics(PageData source, PageData target) {
        target.setCurrentPage(source.getCurrentPage());
        target.setTotalPage(source.getTotalPage());
        target.setTotalItems(source.getTotalItems());
    }

    static class ListData {

        private AbstractFlexibleItem header;
        private PageData<AbstractFlexibleItem> pageData;

        public AbstractFlexibleItem getHeader() {
            return header;
        }

        public void setHeader(AbstractFlexibleItem header) {
            this.header = header;
        }

        public PageData<AbstractFlexibleItem> getPageData() {
            return pageData;
        }

        public void setPageData(PageData<AbstractFlexibleItem> pageData) {
            this.pageData = pageData;
        }
    }
}
