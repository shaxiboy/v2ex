package com.hjx.v2ex;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.facebook.stetho.Stetho;

/**
 * Created by shaxiboy on 2017/3/20 0020.
 */

public class App extends Application {

    public static Context context;
    public static String cacheDir;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        context = getApplicationContext();
        if (getApplicationContext().getExternalCacheDir() != null && existSDCard()) {
            cacheDir = context.getExternalCacheDir().toString();
        } else {
            cacheDir = context.getCacheDir().toString();
        }
    }

    private boolean existSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
