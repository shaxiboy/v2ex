package com.hjx.v2ex.bean;

/**
 * Created by shaxiboy on 2017/4/18 0018.
 */

public class MemberTopicsPage {

    private PageData<Topic> topics;

    public MemberTopicsPage(PageData<Topic> topics) {
        this.topics = topics;
    }

    public PageData<Topic> getTopics() {
        return topics;
    }

    public void setTopics(PageData<Topic> topics) {
        this.topics = topics;
    }
}
