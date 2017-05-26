package com.hjx.v2ex.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.hjx.v2ex.bean.PageData;
import com.hjx.v2ex.flexibleitem.MemberReplyFlexibleItem;
import com.hjx.v2ex.bean.MemberTopicReplies;
import com.hjx.v2ex.bean.Reply;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.network.RetrofitServiceSingleton;

import java.util.Map;

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberRepliesFragment extends ListBaseFragment<MemberTopicReplies> {

    private String memberName;

    public static MemberRepliesFragment newInstance(String memberName) {
        MemberRepliesFragment memberRepliesFragment = new MemberRepliesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DataLoadingBaseActivity.ARG_MEMBERNAME, memberName);
        memberRepliesFragment.setArguments(bundle);
        return memberRepliesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memberName = getArguments().getString(DataLoadingBaseActivity.ARG_MEMBERNAME);

    }

    @Override
    protected void loadData() {
        RetrofitServiceSingleton.getInstance(getActivity().getApplication()).getMemberTopicReplies(memberName, getCurrentPage()).enqueue(getListBaseFragmentCallBack());
    }

    @Override
    ListData getListData(MemberTopicReplies data) {
        ListData listData = new ListData();
        PageData<AbstractFlexibleItem> pageData = new PageData<>();
        copyPageDataStatistics(data.getReplies(), pageData);
        for(Map<Reply, Topic> reply : data.getReplies().getCurrentPageItems()) {
            pageData.getCurrentPageItems().add(new MemberReplyFlexibleItem(reply, null));
        }
        listData.setPageData(pageData);
        return listData;
    }

}
