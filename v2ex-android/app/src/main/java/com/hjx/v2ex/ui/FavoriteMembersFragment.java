package com.hjx.v2ex.ui;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.FavoriteMembers;
import com.hjx.v2ex.bean.Member;
import com.hjx.v2ex.flexibleitem.MemberFlexibleItem;
import com.hjx.v2ex.flexibleitem.ViewMoreFlexibleItem;
import com.hjx.v2ex.network.RetrofitServiceSingleton;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.common.SmoothScrollGridLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteMembersFragment extends ListBaseFragment<FavoriteMembers> {

    @Override
    protected void loadData() {
        RetrofitServiceSingleton.getInstance(getActivity().getApplication()).getFavoriteMembers().enqueue(getListBaseFragmentCallBack());
    }

    @Override
    ListData getListData(FavoriteMembers data) {
        ListData listData = new ListData();
        List<AbstractFlexibleItem> items = new ArrayList<>();
        for(Member member : data.getFavoriteMembers()) {
            items.add(new MemberFlexibleItem(member));
        }
        if(!data.getFavoriteMembers().isEmpty()) {
            items.add(new ViewMoreFlexibleItem(null, ViewMoreFlexibleItem.ViewMoreType.MEMBERSTOPICS));
        }
        listData.setPageData(getOnePageData(items));
        return listData;
    }

    @Override
    RecyclerView.LayoutManager getLayoutManager() {
        GridLayoutManager layoutManager = new SmoothScrollGridLayoutManager(getContext(), 3);
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
