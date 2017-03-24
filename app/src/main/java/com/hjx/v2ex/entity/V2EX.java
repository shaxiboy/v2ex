package com.hjx.v2ex.entity;

/**
 * Created by shaxiboy on 2017/3/22 0022.
 */

public class V2EX {

    private String desc = "V2EX 是创意工作者们的社区。这里目前汇聚了超过 110,000 名主要来自互联网行业、游戏行业和媒体行业的创意工作者。V2EX 希望能够成为创意工作者们的生活和事业的一部分。";
    private int members;
    private int nodes;
    private int topics;
    private int replies;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

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
                "desc='" + desc + '\'' +
                ", members=" + members +
                ", nodes=" + nodes +
                ", topics=" + topics +
                ", replies=" + replies +
                '}';
    }
}
