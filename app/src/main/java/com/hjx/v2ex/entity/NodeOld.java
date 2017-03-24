package com.hjx.v2ex.entity;

/**
 * Created by shaxiboy on 2017/3/2 0002.
 */

public class NodeOld {


    /**
     * id : 1
     * name : babel
     * url : http://www.v2ex.com/go/babel
     * title : Project Babel
     * title_alternative : Project Babel
     * topics : 1119
     * header : Project Babel - 帮助你在云平台上搭建自己的社区
     * footer : V2EX 基于 Project Babel 驱动。Project Babel 是用 Python 语言写成的，运行于 Google App Engine 云计算平台上的社区软件。Project Babel 当前开发分支 2.5。最新版本可以从 <a href="http://github.com/livid/v2ex" target="_blank">GitHub</a> 获取。
     * created : 1272206882
     */

    private int id;
    private String name;
    private String url;
    private String title;
    @com.google.gson.annotations.SerializedName("title_alternative")
    private String titleAlternative;
    private int topics;
    private String header;
    private String footer;
    private int created;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleAlternative() {
        return titleAlternative;
    }

    public void setTitleAlternative(String titleAlternative) {
        this.titleAlternative = titleAlternative;
    }

    public int getTopics() {
        return topics;
    }

    public void setTopics(int topics) {
        this.topics = topics;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "NodeOld{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", titleAlternative='" + titleAlternative + '\'' +
                ", topics=" + topics +
                ", header='" + header + '\'' +
                ", footer='" + footer + '\'' +
                ", created=" + created +
                '}';
    }
}
