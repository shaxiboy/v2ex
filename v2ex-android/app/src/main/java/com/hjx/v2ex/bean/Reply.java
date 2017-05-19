package com.hjx.v2ex.bean;

import com.hjx.v2ex.bean.Member;

import java.io.Serializable;

/**
 * Created by shaxiboy on 2017/3/3 0003.
 */

public class Reply implements Serializable{

    private String content;
    private Member member;
    private String replyTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "content='" + content + '\'' +
                ", member=" + member +
                ", replyTime='" + replyTime + '\'' +
                '}';
    }
}
