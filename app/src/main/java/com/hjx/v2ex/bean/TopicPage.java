package com.hjx.v2ex.bean;

/**
 * Created by shaxiboy on 2017/3/31 0031.
 */

public class TopicPage {

    private Topic topic;
    private PageData<Reply> replies = new PageData<>();

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public PageData<Reply> getReplies() {
        return replies;
    }

    public void setReplies(PageData<Reply> replies) {
        this.replies = replies;
    }
}
