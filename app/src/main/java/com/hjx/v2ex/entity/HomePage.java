package com.hjx.v2ex.entity;

import java.util.List;

/**
 * Created by shaxiboy on 2017/3/31 0031.
 */

public class HomePage {

    private V2EX v2ex;
    private List<Topic> topics;
    private List<Node> hottestNodes;
    private NodesGuide nodesGuide;

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public V2EX getV2ex() {
        return v2ex;
    }

    public void setV2ex(V2EX v2ex) {
        this.v2ex = v2ex;
    }

    public List<Node> getHottestNodes() {
        return hottestNodes;
    }

    public void setHottestNodes(List<Node> hottestNodes) {
        this.hottestNodes = hottestNodes;
    }

    public NodesGuide getNodesGuide() {
        return nodesGuide;
    }

    public void setNodesGuide(NodesGuide nodesGuide) {
        this.nodesGuide = nodesGuide;
    }
}
