package com.hjx.v2ex.bean;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by shaxiboy on 2017/5/3 0003.
 */

public class URLDrawable extends BitmapDrawable {

    private Bitmap bitmap;

    public URLDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
        this.bitmap = bitmap;
    }

    @Override
    public void draw(Canvas canvas) {
        if(bitmap != null) {
            canvas.drawBitmap(bitmap, getBounds().left, getBounds().top, getPaint());
        }
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;

    }
}
