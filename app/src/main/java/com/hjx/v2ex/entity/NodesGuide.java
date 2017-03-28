package com.hjx.v2ex.entity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaxiboy on 2017/3/24 0024.
 */

public class NodesGuide {

    private Map<String, List<Node>> nodeSections = new LinkedHashMap<>();

    public Map<String, List<Node>> getNodeSections() {
        return nodeSections;
    }

    public void setNodeSections(Map<String, List<Node>> nodeSections) {
        this.nodeSections = nodeSections;
    }

    @Override
    public String toString() {
        return "NodesGuide{" +
                "nodeSections=" + nodeSections +
                '}';
    }
}
