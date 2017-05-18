package com.hjx.v2ex;

import android.app.Application;
import android.os.Environment;

/**
 * Created by shaxiboy on 2017/3/20 0020.
 */

public class App extends Application {

    public static String cacheDir;

    @Override
    public void onCreate() {
        super.onCreate();
        if (getApplicationContext().getExternalCacheDir() != null && existSDCard()) {
            cacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            cacheDir = getApplicationContext().getCacheDir().toString();
        }
    }

    private boolean existSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}