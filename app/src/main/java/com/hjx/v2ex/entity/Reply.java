package com.hjx.v2ex.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shaxiboy on 2017/3/3 0003.
 */

public class Reply {

    /**
     * id : 4095409
     * thanks : 9
     * content : 20 块钱哈哈哈哈哈哈哈哈哈哈
     * content_rendered : 20 块钱哈哈哈哈哈哈哈哈哈哈
     * member : {"id":15389,"username":"zzNucker","tagline":"The Code","avatar_mini":"//v2ex.assets.uxengine.net/avatar/7135/86fe/15389_mini.png?m=1400469588","avatar_normal":"//v2ex.assets.uxengine.net/avatar/7135/86fe/15389_normal.png?m=1400469588","avatar_large":"//v2ex.assets.uxengine.net/avatar/7135/86fe/15389_large.png?m=1400469588"}
     * created : 1488454732
     * last_modified : 1488454732
     */

    private int id;
    private int thanks;
    private String content;
    @SerializedName("content_rendered")
    private String contentRendered;
    private Member member;
    private int created;
    @SerializedName("last_modified")
    private int lastModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getThanks() {
        return thanks;
    }

    public void setThanks(int thanks) {
        this.thanks = thanks;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentRendered() {
        return contentRendered;
    }

    public void setContentRendered(String contentRendered) {
        this.contentRendered = contentRendered;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getLastModified() {
        return lastModified;
    }

    public void setLastModified(int lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", thanks=" + thanks +
                ", content='" + content + '\'' +
                ", contentRendered='" + contentRendered + '\'' +
                ", member=" + member +
                ", created=" + created +
                ", lastModified=" + lastModified +
                '}';
    }
}
