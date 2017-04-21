package com.hjx.v2ex.entity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaxiboy on 2017/3/31 0031.
 */

public class HomePage {

    private V2EX v2ex;
    private List<Topic> topics;
    private List<Node> hottestNodes;
    private Map<String, List<Node>> nodeGuide;

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

    public Map<String, List<Node>> getNodeGuide() {
        return nodeGuide;
    }

    public void setNodeGuide(Map<String, List<Node>> nodeGuide) {
        this.nodeGuide = nodeGuide;
    }
}
