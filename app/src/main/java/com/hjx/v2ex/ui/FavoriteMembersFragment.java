package com.hjx.v2ex.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.FavoriteMembers;
import com.hjx.v2ex.bean.Member;
import com.hjx.v2ex.bean.PageData;
import com.hjx.v2ex.flexibleitem.MemberFlexibleItem;
import com.hjx.v2ex.flexibleitem.ViewMoreFlexibleItem;
import com.hjx.v2ex.network.RetrofitSingleton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollGridLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteMembersFragment extends ListBaseFragment<FavoriteMembers> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void loadData() {
        RetrofitSingleton.getInstance(getContext()).getFavoriteMembers().enqueue(getListBaseFragmentCallBack());
    }

    @Override
    PageData<AbstractFlexibleItem> getPageData(FavoriteMembers data) {
        List<AbstractFlexibleItem> items = new ArrayList<>();
        for(Member member : data.getFavoriteMembers()) {
            items.add(new MemberFlexibleItem(member));
        }
        if(!data.getFavoriteMembers().isEmpty()) {
            items.add(new ViewMoreFlexibleItem(null, ViewMoreFlexibleItem.ViewMoreType.MEMBERSTOPICS));
        }
        return getOnePageData(items);
    }

    @Override
    RecyclerView.LayoutManager getLayoutManager() {
        GridLayoutManager layoutManager = new SmoothScrollGridLayoutManager(getActivity(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (getListAdapter().getItemViewType(position)) {
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
