package com.hjx.v2ex.server;

import com.jfinal.core.JFinal;

/**
 * Created by shaxiboy on 2017/5/17 0017.
 */
public class V2EXServerStart {
    public static void main(String[] args) {
        JFinal.start("src/main/webapp", 8080, "/v2ex");
    }
}
