package com.hjx.v2ex.entity;

import com.google.gson.annotations.SerializedName;

import static android.R.attr.id;

/**
 * Created by shaxiboy on 2017/3/3 0003.
 */

public class Member {

    private String username;
    private String photo;
    private String joinTime;
    private String todayRanking;
    private String location;
    private String website;
    private String github;

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

    public String getTodayRanking() {
        return todayRanking;
    }

    public void setTodayRanking(String todayRanking) {
        this.todayRanking = todayRanking;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    @Override
    public String toString() {
        return "Member{" +
                "username='" + username + '\'' +
                ", photo='" + photo + '\'' +
                ", joinTime='" + joinTime + '\'' +
                ", todayRanking='" + todayRanking + '\'' +
                ", location='" + location + '\'' +
                ", website='" + website + '\'' +
                ", github='" + github + '\'' +
                '}';
    }
}
