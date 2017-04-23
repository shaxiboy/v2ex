package com.hjx.v2ex.bean;

import java.util.Map;

/**
 * Created by shaxiboy on 2017/4/18 0018.
 */

public class MemberTopicRepliesPage {

    private PageData<Map<Reply, Topic>> replies;

    public MemberTopicRepliesPage(PageData<Map<Reply, Topic>> topics) {
        this.replies = topics;
    }

    public PageData<Map<Reply, Topic>> getReplies() {
        return replies;
    }

    public void setReplies(PageData<Map<Reply, Topic>> replies) {
        this.replies = replies;
    }
}
