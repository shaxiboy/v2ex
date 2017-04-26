package com.hjx.v2ex.bean;

/**
 * Created by shaxiboy on 2017/4/24 0024.
 */

public class SignoutResult {

    private boolean signout = true;
    private int newSessionId = -1;

    public boolean isSignout() {
        return signout;
    }

    public void setSignout(boolean signout) {
        this.signout = signout;
    }

    public int getNewSessionId() {
        return newSessionId;
    }

    public void setNewSessionId(int newSessionId) {
        this.newSessionId = newSessionId;
    }
}
