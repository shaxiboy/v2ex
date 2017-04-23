package com.hjx.v2ex.bean;

/**
 * Created by shaxiboy on 2017/4/17 0017.
 */

public class MemberMoreInfo {

    private String info;
    private String link;
    private String photo;

    public MemberMoreInfo(String info, String link, String photo) {
        this.info = info;
        this.link = link;
        this.photo = photo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "MemberMoreInfo{" +
                "info='" + info + '\'' +
                ", link='" + link + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
