package com.example.mjn.mdrefresh.header.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.example.mjn.mdrefresh.R;
import com.example.mjn.mdrefresh.header.RentalsSunHeaderView;
import com.example.mjn.mdrefresh.utils.Constant;
import com.example.mjn.mdrefresh.utils.PtrLocalDisplay;

/**
 * Created by Administrator on 2015/10/10.
 *
 */
public class Sun extends Drawable{
    private float mOffsetX;
    private float mStartmOffsetY;
    private float mSunSize;
    private float mDiameter;
    private float mTotalDragDistance;
    private float mCanvasTop;
    private float mRotate;
    private Bitmap mSun;
    private Matrix mMatrix;
    private float mDragPercent;
    private double mAngle;
    private RentalsSunHeaderView mParent;
    private int offset;

    //动画相关属性
    private boolean mBeginSunDown; //是否开始下降的动画
    private boolean mIsOnTheTop;  //是否在顶部
    private boolean mIsDragRelease;  //是否手已经释放
    private boolean mIsAnimationStarted; //是否已开始动画

    private boolean mIsRefreshComplete = false; //网络请求完成

    private boolean mCanTouch = true;
    private boolean mShowSun = true;

    private Animation sunRotateAnimation;
    private Interpolator linearInterpolator = new LinearInterpolator();

    public void setCanvasTop(float top){
        this.mCanvasTop = top;
    }

    public void setDragPercent(float dragPercent){
        this.mDragPercent = dragPercent;
    }

    public void setRotate(float rotate){
        this.mRotate = rotate;
    }

    public void setIsDragRelease(boolean release){
        this.mIsDragRelease = release;
    }

    public boolean getCanTouch(){
        return  mCanTouch;
    }

    public void refreshComplete(){
        mIsRefreshComplete = true;
    }

    public void setmOffset(int offset){
        this.offset = offset;
    }

    public Sun(Context context,RentalsSunHeaderView parent) {
        mParent = parent;
        mSun = BitmapFactory.decodeResource(context.getResources(), R.drawable.moon);
        mMatrix = new Matrix();
        mTotalDragDistance = PtrLocalDisplay.dp2px(Constant.TOTAL_DRAG_DISTANCE);
    }
    public Sun(Context context,float startOffsetX,float startmOffsetY,float diameter,RentalsSunHeaderView parent){
        this(context,parent);
        this.mStartmOffsetY = startmOffsetY;
        if(mSun != null) {
            this.mSunSize = mSun.getWidth();
        }
        this.mDiameter = diameter;

    }
    public Sun(Context context,float startOffsetX,float startOffsetY,float sunSize,float diameter,RentalsSunHeaderView parent){
        this(context,startOffsetX,startOffsetY,diameter,parent);
        this.mSunSize = sunSize;
    }

    @Override
    public void draw(Canvas canvas)  {
        //如果 sun decode失败了，直接返回，不绘制
        if(mSun == null)
        {
            return;
        }
        if(!mShowSun) return;
        Matrix matrix = mMatrix;
        matrix.reset();

        float dragPercent = mDragPercent;

        if (dragPercent > 1.0f) { // Slow down if pulling over set height
            dragPercent = (dragPercent + 9.0f) / 10;
        }

        double angle;
        if(!mBeginSunDown){
            angle = 3.14 / 2 * dragPercent;
        }else{
            angle = mAngle;
        }

        float mOffsetY;
        if(!mIsOnTheTop ) {
            mOffsetY = (float) (mStartmOffsetY - mStartmOffsetY * 0.7 * Math.sin(angle) - mSunSize/2) + (mTotalDragDistance - mCanvasTop)-offset + PtrLocalDisplay.dp2px(115);
            mOffsetX = (float) (mDiameter / 2 - (mDiameter + mSunSize) / 2 * Math.cos(angle) - mSunSize/2);

            if(mOffsetX >= mDiameter / 2 - mSunSize/2 && !mIsDragRelease){
                mIsOnTheTop = true;
            }else if(angle >= 3.14/2 && mIsDragRelease && !mBeginSunDown){
                mIsOnTheTop = true;
            }

        }else{
            mOffsetY = (float) (mStartmOffsetY - mStartmOffsetY * 0.7 - mSunSize/2)+ (mTotalDragDistance - mCanvasTop)-offset + PtrLocalDisplay.dp2px(115) ;
            mOffsetX = (float) mDiameter/2 - mSunSize/2;
            if(angle < 3.14/2 && !mIsAnimationStarted && !mIsDragRelease){
                mIsOnTheTop = false;
            }

        }

        if(mIsDragRelease && !mIsAnimationStarted  && mIsOnTheTop ){
            setupTopRotateAnim(canvas);
        }

        matrix.postTranslate(mOffsetX, mOffsetY);

        mOffsetX += mSunSize/2;
        mOffsetY += mSunSize/2;
        float r = (mIsDragRelease ? -360 : 360) * mRotate ;
        matrix.postRotate(r, mOffsetX, mOffsetY);

        canvas.drawBitmap(mSun, matrix, null);
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

    private void setupTopRotateAnim(final Canvas canvas){
        sunRotateAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setSunDownRotate(interpolatedTime, true);
            }
        };
        sunRotateAnimation.setInterpolator(linearInterpolator);
        sunRotateAnimation.setDuration(Constant.REFRESH_DURATION / 2);
        sunRotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimationStarted = true;
                mCanTouch = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mIsRefreshComplete) {
                    mBeginSunDown = true;
                    mIsOnTheTop = false;
                    setupSunDownAnimations(true);
                } else {
                    if (mIsAnimationStarted) {
                        //重置动画时不再执行
                        mParent.startAnimation(sunRotateAnimation);
                    }
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mIsRefreshComplete = false;
        mParent.startAnimation(sunRotateAnimation);
        mParent.postDelayed(new Runnable() {
            @Override
            public void run() {
                mParent.refreshComplete();
            }
        }, Constant.ANIMATION_TIMEOUT_DURATION);
    }

    private boolean mOnce = true;

    private void setupSunDownAnimations(final boolean reachTop){
        mAngle = 3.14/2;
        final float backToTopAngle = 3.14f * 4 / 5;
        AnimationSet animationSet = new AnimationSet(true);
        Animation sunDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setSunDownRotate(interpolatedTime, reachTop);
                if (mAngle > backToTopAngle && mOnce) {
                    mParent.smoothTo();
                    mOnce = false;
                }
            }
        };
        sunDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                stopAnimation();
                mIsAnimationStarted = false;
                mShowSun = false;
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        sunDownAnimation.setInterpolator(linearInterpolator);
        sunDownAnimation.setDuration(Constant.SUN_DOWN_ANIMATION_DURATION);
        animationSet.addAnimation(sunDownAnimation);
        animationSet.setFillAfter(true);
        mParent.startAnimation(animationSet);
    }

    public void setSunDownRotate(float interpolatedTime,boolean reachTop){
        if(reachTop){
            mAngle = 3.14 / 2 + interpolatedTime * 3.14 / 2;
        }else{
            mAngle = 3.14 /2 - interpolatedTime *3.14/2;
        }

        mRotate = interpolatedTime;
        mParent.invalidate();
    }

    private void stopAnimation(){
        mIsAnimationStarted = false;
        mParent.clearAnimation();
        setRotate(0);
        mAngle = 0;
        mOffsetX = 0;
        mBeginSunDown = false;
        mIsDragRelease = false;
        mIsOnTheTop = false;
        mCanTouch = true;
        mOnce = true;
        mShowSun = true;
        mIsRefreshComplete = false;
        mParent.invalidate();
    }

    public void reset(){
        stopAnimation();
    }

}
