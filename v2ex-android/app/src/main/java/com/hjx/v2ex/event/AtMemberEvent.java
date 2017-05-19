package com.hjx.v2ex.event;

/**
 * Created by shaxiboy on 2017/5/19 0019.
 */

public class AtMemberEvent {

    private String memberName;

    public AtMemberEvent(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}
