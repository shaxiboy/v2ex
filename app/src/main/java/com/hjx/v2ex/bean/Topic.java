package com.hjx.v2ex.bean;

import java.util.List;

/**
 * Created by shaxiboy on 2017/3/3 0003.
 */

public class Topic {

    private int id;
    private String title;
    private String content = "";
    private int replyNum;
    private int collectedNum;
    private int thanksNum;
    private String favoriteURL;
    private String thankToken;
    private Member member;
    private Node node;
    private String createdTime;
    private String lastRepliedTime;
    private List<PS> psList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(int replyNum) {
        this.replyNum = replyNum;
    }

    public int getCollectedNum() {
        return collectedNum;
    }

    public void setCollectedNum(int collectedNum) {
        this.collectedNum = collectedNum;
    }

    public int getThanksNum() {
        return thanksNum;
    }

    public void setThanksNum(int thanksNum) {
        this.thanksNum = thanksNum;
    }

    public String getFavoriteURL() {
        return favoriteURL;
    }

    public void setFavoriteURL(String favoriteURL) {
        this.favoriteURL = favoriteURL;
    }

    public String getThankToken() {
        return thankToken;
    }

    public void setThankToken(String thankToken) {
        this.thankToken = thankToken;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getLastRepliedTime() {
        return lastRepliedTime;
    }

    public void setLastRepliedTime(String lastRepliedTime) {
        this.lastRepliedTime = lastRepliedTime;
    }

    public List<PS> getPsList() {
        return psList;
    }

    public void setPsList(List<PS> psList) {
        this.psList = psList;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", replyNum=" + replyNum +
                ", collectedNum=" + collectedNum +
                ", thanksNum=" + thanksNum +
                ", favoriteURL='" + favoriteURL + '\'' +
                ", thankToken='" + thankToken + '\'' +
                ", member=" + member +
                ", node=" + node +
                ", createdTime='" + createdTime + '\'' +
                ", lastRepliedTime='" + lastRepliedTime + '\'' +
                ", psList=" + psList +
                '}';
    }

    public static class PS {
        private String time;
        private String content;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "PS{" +
                    "time='" + time + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }
}
