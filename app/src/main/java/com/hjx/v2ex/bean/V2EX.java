package com.hjx.v2ex.bean;

/**
 * Created by shaxiboy on 2017/3/22 0022.
 */

public class V2EX {

    private int members;
    private int nodes;
    private int topics;
    private int replies;

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public int getNodes() {
        return nodes;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    public int getTopics() {
        return topics;
    }

    public void setTopics(int topics) {
        this.topics = topics;
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }

    @Override
    public String toString() {
        return "V2EX{" +
                "members=" + members +
                ", nodes=" + nodes +
                ", topics=" + topics +
                ", replies=" + replies +
                '}';
    }
}
