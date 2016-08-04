package com.example.mjn.mdrefresh.header.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.example.mjn.mdrefresh.R;
import com.example.mjn.mdrefresh.RefreshUIListener;
import com.example.mjn.mdrefresh.utils.Constant;


/**
 * Created by Administrator on 2015/10/10.
 *
 */
public class Building extends Drawable implements RefreshUIListener{

    private Context mContext;

    private int mBuildingWidth;
    private int mBuildingHeight;
    private int mShowWidth;
    private int mShowHeight;

    private float mInitHeaderHeight;
    private float mScreenWidth;

    private Bitmap mBuilding;
    private float mDragPercent;


    private Matrix mDrawMatrix;

    private static final float TOWN_INITIAL_SCALE = 1.0f;
    private static final float TOWN_FINAL_SCALE = 1.2f;

    public Building(Context context,View parent) {
        mContext = context;
        mInitHeaderHeight = Constant.dp2px(Constant.SKY_HEIGHT);
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;

        setImageResource(R.drawable.home_title_building_default);
    }

    private void initBitmapConfig(){
        if(mBuilding != null) {
            mBuildingWidth = mBuilding.getWidth();
            mBuildingHeight = mBuilding.getHeight();
        }
        mShowWidth = Constant.dp2px(300);
        mShowHeight = Constant.dp2px(100);

        // ImageView CenterCrop
        mDrawMatrix = new Matrix();
        float scale;
        float dx = 0, dy = 0;
        //实际上相当于mBuildingWidth/mBuildingHeight > mShowWidth / mShowHeight,此时scale则以height比例为标准。
        if (mBuildingWidth * mShowHeight > mShowWidth * mBuildingHeight) {
            scale = (float) mShowHeight / (float) mBuildingHeight;
            dx = (mShowWidth - mBuildingWidth * scale) * 0.5f;   //x方向的微调
        } else {
            scale = (float) mShowWidth / (float) mBuildingWidth;
            dy = (mShowHeight - mBuildingHeight * scale) * 0.5f; //y方向上的微调
        }

        mDrawMatrix.setScale(scale, scale);
        mDrawMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
    }

    public void setImageResource(int resId){
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
        setImageResource(bitmap);
    }

    public void setImageResource(Bitmap bitmap){
        if(mBuilding != null){
            mBuilding.recycle();
        }
        mBuilding = bitmap;
        initBitmapConfig();
        invalidateSelf();
    }

    public void setDragPercent(float dragPercent){
        if(dragPercent < 0){
            mDragPercent = 0;
        }else if(dragPercent > 1){
            mDragPercent = 1;
        }else {
            mDragPercent = dragPercent;
        }
    }


    @Override
    public void setOffset(int offset) {
        float percent = (offset-Constant.dp2px(Constant.DEFAULT_HEADER_HEIGHT))/(Constant.dp2px(100)*1.0f);
        setDragPercent(percent);
    }

    @Override
    public void draw(Canvas canvas) {
        //如果bitmap decode失败了的话，直接不绘制
        if(mBuilding == null){
            return;
        }
        canvas.save();
        float scale = TOWN_INITIAL_SCALE + mDragPercent * (TOWN_FINAL_SCALE - TOWN_INITIAL_SCALE);
        canvas.translate((mScreenWidth - mShowWidth * scale) / 2, mInitHeaderHeight - mShowHeight * scale);
        canvas.scale(scale, scale);
        canvas.clipRect(0, 0, mShowWidth, mShowHeight);
        canvas.drawBitmap(mBuilding, mDrawMatrix, null);
        canvas.restore();
    }

    @Override
    public void refreshComplete() {

    }

    @Override
    public void resetOriginals() {

    }

    @Override
    public void offsetTopAndBottom(int offset) {

    }

    @Override
    public void setIsReleaseDrag(boolean isReleaseDrag) {

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
