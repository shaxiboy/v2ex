package com.hjx.v2ex.bean;

/**
 * Created by shaxiboy on 2017/5/20 0020.
 */

public class Notification {

    private Member member;
    private Topic topic;
    private Reply reply;
    private String time;
    private NotificationType type;

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public enum NotificationType {
        REPLY, AT, FAVORITE, THANK
    }
}
