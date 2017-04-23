package com.hjx.v2ex.bean;

/**
 * Created by shaxiboy on 2017/4/14 0014.
 */

public class NodePage {

    private Node node;
    private PageData<Topic> topics;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public PageData<Topic> getTopics() {
        return topics;
    }

    public void setTopics(PageData<Topic> topics) {
        this.topics = topics;
    }
}
