package com.hjx.v2ex.util;

import android.text.format.DateUtils;

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
}
