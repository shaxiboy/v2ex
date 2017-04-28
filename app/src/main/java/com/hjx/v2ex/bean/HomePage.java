package com.hjx.v2ex.bean;

import java.util.List;
import java.util.Map;

/**
 * Created by shaxiboy on 2017/3/31 0031.
 */

public class HomePage {

    private V2EXStatistics v2ExStatistics;
    private List<Topic> topics;
    private List<Node> hottestNodes;
    private Map<String, List<Node>> nodeGuide;

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public V2EXStatistics getV2ExStatistics() {
        return v2ExStatistics;
    }

    public void setV2ExStatistics(V2EXStatistics v2ExStatistics) {
        this.v2ExStatistics = v2ExStatistics;
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
