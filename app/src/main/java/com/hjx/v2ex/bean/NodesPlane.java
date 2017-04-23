package com.hjx.v2ex.bean;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaxiboy on 2017/3/22 0022.
 */

public class NodesPlane {

    private int nodeCount;
    private Map<String, List<Node>> nodeSections = new LinkedHashMap<>();

    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public Map<String, List<Node>> getNodeSections() {
        return nodeSections;
    }
}
