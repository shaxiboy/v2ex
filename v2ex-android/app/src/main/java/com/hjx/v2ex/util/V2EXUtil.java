package com.hjx.v2ex.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import com.hjx.v2ex.bean.SigninResult;
import com.hjx.v2ex.event.ShowMemberRepliesEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Pattern;

/**
 * Created by shaxiboy on 2017/3/13 0013.
 */

public class V2EXUtil {

    public static String getNumber(String text) {
        return Pattern.compile("[^0-9]").matcher(text).replaceAll("");
    }

    public static boolean isLogin(Context context) {
        return readLoginResult(context) != null;
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
//            e.printStackTrace();
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
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
        if(!TextUtils.isEmpty(msg)) progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    public static Spanned fromHtml(String html, Html.ImageGetter imageGetter, Html.TagHandler tagHandler, final int replyPosition){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY, imageGetter, tagHandler);
        } else {
            result = Html.fromHtml(html, imageGetter, tagHandler);
        }
        SpannableStringBuilder htmlSpannable;
        if (result instanceof SpannableStringBuilder) {
            htmlSpannable = (SpannableStringBuilder) result;
        } else {
            htmlSpannable = new SpannableStringBuilder(result);
        }
        ImageSpan[] spans = htmlSpannable.getSpans(0, htmlSpannable.length(), ImageSpan.class);
        for(ImageSpan span : spans){
            final int start = htmlSpannable.getSpanStart(span);
            final int end   = htmlSpannable.getSpanEnd(span);
            URLSpan urlSpan = new URLSpan(span.getSource());
            URLSpan[] urlSpens = htmlSpannable.getSpans(start, end, URLSpan.class);
            if(urlSpens == null || urlSpens.length == 0) {
                htmlSpannable.setSpan(urlSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        URLSpan[] urlSpens = htmlSpannable.getSpans(0, htmlSpannable.length(), URLSpan.class);
        for(URLSpan urlSpan : urlSpens) {
            final String url = urlSpan.getURL();
            if(url.startsWith("/member/")) {
                int start = htmlSpannable.getSpanStart(urlSpan);
                int end = htmlSpannable.getSpanEnd(urlSpan);
                htmlSpannable.removeSpan(urlSpan);
                htmlSpannable.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        if(replyPosition != -1) {
                            EventBus.getDefault().post(new ShowMemberRepliesEvent(url.split("/")[2], replyPosition));
                        }
                    }
                }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return htmlSpannable;
    }

    public static int getDisplayHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int getDisplayWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int dp(Context context, float dp){
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }
}
