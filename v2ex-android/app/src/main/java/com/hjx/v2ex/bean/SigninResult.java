package com.hjx.v2ex.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by shaxiboy on 2017/4/24 0024.
 */

public class SigninResult implements Serializable{

    private boolean sigin;
    private int signoutOnce = -1;
    private String name;
    private String photo;
    private String errorMsg;

    public boolean isSigin() {
        return sigin;
    }

    public void setSigin(boolean sigin) {
        this.sigin = sigin;
    }

    public int getSignoutOnce() {
        return signoutOnce;
    }

    public void setSignoutOnce(int signoutOnce) {
        this.signoutOnce = signoutOnce;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
