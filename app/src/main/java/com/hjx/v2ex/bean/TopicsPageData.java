package com.hjx.v2ex.bean;

import com.hjx.v2ex.ui.ListBaseFragment;

/**
 * Created by shaxiboy on 2017/4/27 0027.
 */

public class TopicsPageData implements ListBaseFragment.ListBaseFragmentData{

    private PageData<Topic> topics = new PageData<>();

    public PageData<Topic> getTopics() {
        return topics;
    }

    public void setTopics(PageData<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public PageData getPageData() {
        return topics;
    }
}
