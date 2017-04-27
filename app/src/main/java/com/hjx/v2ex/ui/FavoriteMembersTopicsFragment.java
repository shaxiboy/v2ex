package com.hjx.v2ex.ui;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.MemberTopicsPage;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.flexibleitem.ProgressItem;
import com.hjx.v2ex.flexibleitem.TopicFlexibleItem;
import com.hjx.v2ex.network.RetrofitSingleton;

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
public class FavoriteMembersTopicsFragment extends ListBaseFragment {

    @Override
    protected void loadData() {
        RetrofitSingleton.getInstance(getContext()).getFavoriteMembersTopics(getCurrentPage()).enqueue(getListBaseFragmentCallBack());
    }

    @Override
    AbstractFlexibleItem getFlexibleItem(Object item) {
        return new TopicFlexibleItem((Topic) item);
    }
}
