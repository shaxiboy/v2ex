package com.hjx.v2ex.bean;

/**
 * Created by shaxiboy on 2017/4/24 0024.
 */

public class SigninParams {

    private String name;//登录表单用户名关键字
    private String password;//登录表单密码关键字
    private String code;//登录表单验证码关键字
    private String codeImg;//登录表单验证码图片地址
    private String once;//登录表单once关键字的值
    private String next = "https://www.v2ex.com/mission";//登录表单next关键字的值

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeImg() {
        return codeImg;
    }

    public void setCodeImg(String codeImg) {
        this.codeImg = codeImg;
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
                ", code='" + code + '\'' +
                ", codeImg='" + codeImg + '\'' +
                ", once='" + once + '\'' +
                ", next='" + next + '\'' +
                '}';
    }
}
