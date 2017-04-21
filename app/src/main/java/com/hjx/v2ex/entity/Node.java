package com.hjx.v2ex.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaxiboy on 2017/3/2 0002.
 */

public class Node implements Serializable{

    private int id;
    private String name;
    private String title;
    private String desc;
    private String photo;
    private int topicNum;
    private String collectHref;
    private Node parent;
    private List<Node> children = new ArrayList<>();
    private List<Node> relatives = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getCollectHref() {
        return collectHref;
    }

    public void setCollectHref(String collectHref) {
        this.collectHref = collectHref;
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

    public List<Node> getRelatives() {
        return relatives;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", photo='" + photo + '\'' +
                ", topicNum=" + topicNum +
                ", collectHref='" + collectHref + '\'' +
                ", parent=" + parent +
                ", children=" + children +
                ", relatives=" + relatives +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return name.equals(node.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
