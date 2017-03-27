package com.hjx.v2ex.entity;

/**
 * Created by shaxiboy on 2017/3/3 0003.
 */

public class Member {

    private String username;
    private String photo;
    private String joinTime;
    private String noticeHref;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public String getNoticeHref() {
        return noticeHref;
    }

    public void setNoticeHref(String noticeHref) {
        this.noticeHref = noticeHref;
    }

    @Override
    public String toString() {
        return "Member{" +
                "username='" + username + '\'' +
                ", photo='" + photo + '\'' +
                ", joinTime='" + joinTime + '\'' +
                ", noticeHref='" + noticeHref + '\'' +
                '}';
    }
}
