package com.hjx.v2ex.event;

/**
 * Created by shaxiboy on 2017/5/19 0019.
 */

public class ShowMemberRepliesEvent {

    private String memberName;
    private int terminateIndex;

    public ShowMemberRepliesEvent(String memberName, int terminateIndex) {
        this.memberName = memberName;
        this.terminateIndex = terminateIndex;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public int getTerminateIndex() {
        return terminateIndex;
    }

    public void setTerminateIndex(int terminateIndex) {
        this.terminateIndex = terminateIndex;
    }
}
