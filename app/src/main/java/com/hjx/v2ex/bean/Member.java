package com.hjx.v2ex.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaxiboy on 2017/3/3 0003.
 */

public class Member implements Serializable{

    private String username;
    private String photo;
    private String basicInfo;
    private String noticeHref;
    private List<MemberMoreInfo> moreInfos = new ArrayList<>();

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

    public String getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(String basicInfo) {
        this.basicInfo = basicInfo;
    }

    public String getNoticeHref() {
        return noticeHref;
    }

    public void setNoticeHref(String noticeHref) {
        this.noticeHref = noticeHref;
    }

    public List<MemberMoreInfo> getMoreInfos() {
        return moreInfos;
    }

    public void setMoreInfos(List<MemberMoreInfo> moreInfos) {
        this.moreInfos = moreInfos;
    }

    @Override
    public String toString() {
        return "Member{" +
                "username='" + username + '\'' +
                ", photo='" + photo + '\'' +
                ", basicInfo='" + basicInfo + '\'' +
                ", noticeHref='" + noticeHref + '\'' +
                ", moreInfos=" + moreInfos +
                '}';
    }
}
