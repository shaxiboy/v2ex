package com.hjx.v2ex.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.format.DateUtils;

import com.hjx.v2ex.MainActivity;
import com.hjx.v2ex.bean.SigninResult;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by shaxiboy on 2017/3/13 0013.
 */

public class V2EXUtil {

    public static String parseTime(String time) {
        if(time.contains("小时")) {
            return time.subSequence(0, time.indexOf("小时")) + "小时前";
        }
        return time;
    }

    public static String parseTime(long longTime) {
        if(longTime == -1) return "";
        long created = longTime * 1000;
        long now = System.currentTimeMillis();
        long difference = now - created;
        CharSequence text = (difference >= 0 && difference <= DateUtils.MINUTE_IN_MILLIS) ?
                "刚刚" :
                DateUtils.getRelativeTimeSpanString(
                        created,
                        now,
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE);
        return text.toString();
    }

    public static boolean isLogin(Context context) {
        if(readLoginResult(context) != null) {
            return true;
        }
        return false;
    }

    public static void writeLoginResult(Context context, SigninResult signinResult) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput("signin", Context.MODE_PRIVATE));
            oos.writeObject(signinResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SigninResult readLoginResult(Context context) {
        try {
            ObjectInputStream ois = new ObjectInputStream(context.openFileInput("signin"));
            return (SigninResult) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearLoginResult(Context context) {
        File signinFile = new File(context.getFilesDir(), "signin");
        if(signinFile.exists()) {
            signinFile.delete();
        }
    }

    public static ProgressDialog showProgressDialog(Context context, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }
}
