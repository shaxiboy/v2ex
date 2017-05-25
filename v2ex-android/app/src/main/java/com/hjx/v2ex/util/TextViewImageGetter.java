package com.hjx.v2ex.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hjx.v2ex.R;
import com.hjx.v2ex.util.V2EXUtil;

/**
 * Created by shaxiboy on 2017/5/7 0007.
 */

public class TextViewImageGetter implements Html.ImageGetter{

    private Context context;
    private TextView textView;
    private int maxWidth;

    public TextViewImageGetter(Context context, TextView textView, int maxWidth) {
        this.context = context;
        this.textView = textView;
        this.maxWidth = maxWidth;
    }

    @Override
    public Drawable getDrawable(String url) {
        final LevelListDrawable levelListDrawable = new LevelListDrawable();
        Drawable empty = ContextCompat.getDrawable(context, R.drawable.ic_sync_white_24dp);
        levelListDrawable.addLevel(0, 0, empty);
        levelListDrawable.setBounds(0, 0, 50, 50);
        Glide.with(context)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        int width;
                        int height;
                        if (resource.getWidth() > maxWidth) {
                            width = maxWidth;
                            height = maxWidth * resource.getHeight() / resource.getWidth();
                        } else {
                            width = resource.getWidth();
                            height = resource.getHeight();
                        }
                        Drawable target = new BitmapDrawable(context.getResources(), resource);
                        levelListDrawable.addLevel(1, 1, target);
                        levelListDrawable.setBounds(0, 0, width, height);
                        levelListDrawable.setLevel(1);
                        textView.setText(textView.getText());
                    }
                });
        return levelListDrawable;
    }
}
