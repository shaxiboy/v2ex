package com.hjx.v2ex.bean;

/**
 * Created by shaxiboy on 2017/5/2 0002.
 */

public class NewTopicResult {

    private boolean success;
    private int newOnce;
    private String failedMsg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getNewOnce() {
        return newOnce;
    }

    public void setNewOnce(int newOnce) {
        this.newOnce = newOnce;
    }

    public String getFailedMsg() {
        return failedMsg;
    }

    public void setFailedMsg(String failedMsg) {
        this.failedMsg = failedMsg;
    }
}
