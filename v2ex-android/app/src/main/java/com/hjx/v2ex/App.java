package com.hjx.v2ex;

import android.app.Application;

import com.tencent.bugly.Bugly;

/**
 * Created by shaxiboy on 2017/3/20 0020.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "94c3afbe6c", true);
    }
}
