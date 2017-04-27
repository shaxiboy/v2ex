package com.hjx.v2ex.ui;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.FavoriteMembers;
import com.hjx.v2ex.bean.FavoriteNodes;
import com.hjx.v2ex.bean.Member;
import com.hjx.v2ex.bean.Node;
import com.hjx.v2ex.flexibleitem.MemberFlexibleItem;
import com.hjx.v2ex.flexibleitem.NodeFlexibleItem;
import com.hjx.v2ex.flexibleitem.ViewMoreFlexibleItem;
import com.hjx.v2ex.network.RetrofitSingleton;

import java.util.ArrayList;

import butterknife.BindView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollGridLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteMembersFragment extends DataLoadingBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private FlexibleAdapter favoriteMembersAdapter;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    public int getContentRes() {
        return R.layout.recycler_view_layout;
    }

    @Override
    protected void initView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        favoriteMembersAdapter = new FlexibleAdapter(new ArrayList());
        recyclerView.setAdapter(favoriteMembersAdapter);
        recyclerView.setLayoutManager(createLayoutManager());
    }

    @Override
    protected void loadData() {
        loadFavoriteMembers();
    }

    @Override
    public void onRefresh() {
        loadFavoriteMembers();
    }

    private void loadFavoriteMembers() {
        RetrofitSingleton.getInstance(getContext()).favoriteMembersPage().enqueue(new Callback<FavoriteMembers>() {
            @Override
            public void onResponse(Call<FavoriteMembers> call, Response<FavoriteMembers> response) {
                swipeRefreshLayout.setRefreshing(false);
                favoriteMembersAdapter.clear();
                FavoriteMembers favoriteMembers = response.body();
                if (favoriteMembers != null) {
                    successLoadingData();
                    for (Member member : favoriteMembers.getFavoriteMembers()) {
                        favoriteMembersAdapter.addItem(new MemberFlexibleItem(member));
                    }
                    if(!favoriteMembers.getFavoriteMembers().isEmpty()) {
                        favoriteMembersAdapter.addItem(new ViewMoreFlexibleItem(null, ViewMoreFlexibleItem.ViewMoreType.MEMBERSTOPICS));
                    }
                } else {
                    errorLoadingData();
                }
            }

            @Override
            public void onFailure(Call<FavoriteMembers> call, Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                favoriteMembersAdapter.clear();
                errorLoadingData();
                throwable.printStackTrace();
            }
        });
    }

    private RecyclerView.LayoutManager createLayoutManager() {
        GridLayoutManager layoutManager = new SmoothScrollGridLayoutManager(getActivity(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (favoriteMembersAdapter.getItemViewType(position)) {
                    case R.layout.recycler_view_item_view_more:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
        return layoutManager;
    }
}
