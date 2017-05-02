package com.hjx.v2ex.bean;

/**
 * Created by shaxiboy on 2017/5/2 0002.
 */

public class ReplyTopicResult {

    private boolean success = true;
    private int replyOnce = -1;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getReplyOnce() {
        return replyOnce;
    }

    public void setReplyOnce(int replyOnce) {
        this.replyOnce = replyOnce;
    }
}
