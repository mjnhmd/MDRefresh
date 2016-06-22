package com.example.mjn.mdrefresh.header.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.example.mjn.mdrefresh.R;


/**
 * Created by Administrator on 2015/9/28.
 *
 */
public class Sky extends Drawable {

    private float mSkyHeight;
    private float mSkyWidth;

    private Context mContext;
    Paint p = new Paint();

    public Sky(Context context,View parent) {
        mContext = context;

    }
    public Sky(Context context,View parent,float skyWidth ,float skyHeight){
        mContext = context;
        mSkyWidth = skyWidth;
        mSkyHeight = skyHeight;
    }

    private void drawDaytime(Canvas canvas){
        p.setColor(mContext.getResources().getColor(R.color.title_background_night));
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, mSkyWidth, mSkyHeight, p);
        Log.d("qiehuan","drawDaytime");
    }

    @Override
    public void draw(Canvas canvas) {
            drawDaytime(canvas);
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
