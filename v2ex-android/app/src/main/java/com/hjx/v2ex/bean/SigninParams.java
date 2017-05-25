package com.hjx.v2ex.bean;

/**
 * Created by shaxiboy on 2017/4/24 0024.
 */

public class SigninParams {

    private String name;
    private String password;
    private String once;
    private String next = "https://www.v2ex.com/mission";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOnce() {
        return once;
    }

    public void setOnce(String once) {
        this.once = once;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return "SigninParams{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", once='" + once + '\'' +
                ", next='" + next + '\'' +
                '}';
    }
}
