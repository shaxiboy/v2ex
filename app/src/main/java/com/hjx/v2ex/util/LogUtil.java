package com.hjx.v2ex.util;

import android.util.Log;

import com.hjx.v2ex.App;
import com.hjx.v2ex.AppConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shaxiboy on 2017/3/20 0020.
 */

public class LogUtil {

    public static boolean isDebug = AppConfig.DEBUG;
    private static final boolean WRITE_TO_FILE = false;
    private static final String LOG_FILE_NAME = "log.txt";

    /**
     * 错误信息
     */
    public static void e(String TAG, String msg) {
        Log.e(TAG, msg);
        if (WRITE_TO_FILE) {
            writeLogtoFile("e", TAG, msg);
        }
    }

    /**
     * 警告信息
     */
    public static void w(String TAG, String msg) {
        if (isDebug) {
            Log.w(TAG, msg);
            if (WRITE_TO_FILE) {
                writeLogtoFile("w", TAG, msg);
            }
        }
    }

    /**
     * 调试信息
     */

    public static void d(String TAG, String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
            if (WRITE_TO_FILE) {
                writeLogtoFile("d", TAG, msg);
            }
        }
    }

    /**
     * 提示信息
     */
    public static void i(String TAG, String msg) {
        if (isDebug) {
            Log.i(TAG, msg);
            if (WRITE_TO_FILE) {
                writeLogtoFile("i", TAG, msg);
            }
        }
    }

    public static void e(String msg) {
        e(getClassName(), msg);
    }

    public static void w(String msg) {
        w(getClassName(), msg);
    }

    public static void d(String msg) { d(getClassName(), msg); }

    public static void i(String msg) {
        i(getClassName(), msg);
    }

    /**
     * @return 当前的类名(simpleName)
     */
    private static String getClassName() {

        String result;
        StackTraceElement thisMethodStack = Thread.currentThread().getStackTrace()[4];
        result = thisMethodStack.getClassName();
        int lastIndex = result.lastIndexOf(".");
        result = result.substring(lastIndex + 1);

        int i = result.indexOf("$");// 剔除匿名内部类名

        return i == -1 ? result : result.substring(0, i);
    }


    /**
     * 写入日志到文件中
     */
    private static void writeLogtoFile(String logtype, String tag, String msg) {
        String needWriteMessage = "\r\n"
                + new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date())
                + "\r\n"
                + logtype
                + "    "
                + tag
                + "\r\n"
                + msg;
        File file = new File(App.cacheDir, LOG_FILE_NAME);
        try {
            FileWriter filerWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断文件夹是否存在,如果不存在则创建文件夹
     */
    public static void isExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }
        }
    }

}
