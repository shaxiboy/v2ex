package com.hjx.v2ex.entity;

import java.util.List;

/**
 * Created by shaxiboy on 2017/3/2 0002.
 */

public class Node {

    private String name;
    private String title;
    private String desc;
    private String photo;
    private int topicNum;
    private Node parent;
    private List<Node> children;
    private List<Node> relatives;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(int topicNum) {
        this.topicNum = topicNum;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public List<Node> getRelatives() {
        return relatives;
    }

    public void setRelatives(List<Node> relatives) {
        this.relatives = relatives;
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", photo='" + photo + '\'' +
                ", topicNum=" + topicNum +
                ", parent=" + parent +
                ", children=" + children +
                ", relatives=" + relatives +
                '}';
    }
}
