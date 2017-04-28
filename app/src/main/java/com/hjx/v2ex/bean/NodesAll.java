package com.hjx.v2ex.bean;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaxiboy on 2017/3/22 0022.
 */

public class NodesAll {

    private int nodeNum;
    private Map<String, List<Node>> nodeSections = new LinkedHashMap<>();

    public int getNodeNum() {
        return nodeNum;
    }

    public void setNodeNum(int nodeNum) {
        this.nodeNum = nodeNum;
    }

    public Map<String, List<Node>> getNodeSections() {
        return nodeSections;
    }
}
