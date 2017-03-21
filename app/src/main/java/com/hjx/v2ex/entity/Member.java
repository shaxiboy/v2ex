package com.hjx.v2ex.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shaxiboy on 2017/3/3 0003.
 */

public class Member {

    /**
     * status : found
     * id : 118929
     * url : http://www.v2ex.com/member/Yc1992
     * username : Yc1992
     * website :
     * twitter :
     * psn :
     * github :
     * btc :
     * location :
     * tagline :
     * bio :
     * avatar_mini : //v2ex.assets.uxengine.net/avatar/b4a7/dc2e/118929_mini.png?m=1442311108
     * avatar_normal : //v2ex.assets.uxengine.net/avatar/b4a7/dc2e/118929_normal.png?m=1442311108
     * avatar_large : //v2ex.assets.uxengine.net/avatar/b4a7/dc2e/118929_large.png?m=1442311108
     * created : 1432630200
     */

    private String status;
    private int id;
    private String url;
    private String username;
    private String website;
    private String twitter;
    private String psn;
    private String github;
    private String btc;
    private String location;
    private String tagline;
    private String bio;
    @SerializedName("avatar_mini")
    private String avatarMini;
    @SerializedName("avatar_normal")
    private String avatarNormal;
    @SerializedName("avatar_large")
    private String avatarLarge;
    private int created;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getPsn() {
        return psn;
    }

    public void setPsn(String psn) {
        this.psn = psn;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getBtc() {
        return btc;
    }

    public void setBtc(String btc) {
        this.btc = btc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarMini() {
        if(avatarMini.startsWith("//")) {
            return "http:" + avatarMini;
        } else {
            return avatarMini;
        }
    }

    public void setAvatarMini(String avatarMini) {
        this.avatarMini = avatarMini;
    }

    public String getAvatarNormal() {
        if(avatarNormal.startsWith("//")) {
            return "http:" + avatarNormal;
        } else {
            return avatarNormal;
        }
    }

    public void setAvatarNormal(String avatarNormal) {
        this.avatarNormal = avatarNormal;
    }

    public String getAvatarLarge() {
        if(avatarLarge.startsWith("//")) {
            return "http:" + avatarLarge;
        } else {
            return avatarLarge;
        }
    }

    public void setAvatarLarge(String avatarLarge) {
        this.avatarLarge = avatarLarge;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Member{" +
                "status='" + status + '\'' +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", website='" + website + '\'' +
                ", twitter='" + twitter + '\'' +
                ", psn='" + psn + '\'' +
                ", github='" + github + '\'' +
                ", btc='" + btc + '\'' +
                ", location='" + location + '\'' +
                ", tagline='" + tagline + '\'' +
                ", bio='" + bio + '\'' +
                ", avatarMini='" + avatarMini + '\'' +
                ", avatarNormal='" + avatarNormal + '\'' +
                ", avatarLarge='" + avatarLarge + '\'' +
                ", created=" + created +
                '}';
    }
}
