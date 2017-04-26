package com.hjx.v2ex.bean;

/**
 * Created by shaxiboy on 2017/4/25 0025.
 */

public class MemberFavoriteResult {

    private String favoriteURL;

    public MemberFavoriteResult(String favoriteURL) {
        this.favoriteURL = favoriteURL;
    }

    public String getFavoriteURL() {
        return favoriteURL;
    }
}
