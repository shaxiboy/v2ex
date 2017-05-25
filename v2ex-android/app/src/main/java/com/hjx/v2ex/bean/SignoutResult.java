package com.hjx.v2ex.bean;

/**
 * Created by shaxiboy on 2017/4/24 0024.
 */

public class SignoutResult {

    private boolean signout = true;
    private int newSignoutOnce = -1;

    public boolean isSignout() {
        return signout;
    }

    public void setSignout(boolean signout) {
        this.signout = signout;
    }

    public int getNewSignoutOnce() {
        return newSignoutOnce;
    }

    public void setNewSignoutOnce(int newSignoutOnce) {
        this.newSignoutOnce = newSignoutOnce;
    }
}
