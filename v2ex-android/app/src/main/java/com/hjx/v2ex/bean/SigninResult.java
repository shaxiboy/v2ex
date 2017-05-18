package com.hjx.v2ex.bean;

import java.io.Serializable;

/**
 * Created by shaxiboy on 2017/4/24 0024.
 */

public class SigninResult implements Serializable{

    private boolean sigin;
    private int sessionId = -1;
    private String name;
    private String photo;
    private String errorMsg;

    public boolean isSigin() {
        return sigin;
    }

    public void setSigin(boolean sigin) {
        this.sigin = sigin;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
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

    public enum SigninResultType {
        SUCCESS, FAILED
    }
}
