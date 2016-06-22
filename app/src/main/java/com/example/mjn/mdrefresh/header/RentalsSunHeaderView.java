package com.example.mjn.mdrefresh.header;

import android.content.Context;
import android.graphics.Canvas;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.example.mjn.mdrefresh.R;
import com.example.mjn.mdrefresh.header.view.Building;
import com.example.mjn.mdrefresh.header.view.Sky;
import com.example.mjn.mdrefresh.header.view.Sun;
import com.example.mjn.mdrefresh.utils.Constant;
import com.example.mjn.mdrefresh.utils.PtrLocalDisplay;


public class RentalsSunHeaderView extends View implements View.OnTouchListener, AppBarLayout.OnOffsetChangedListener{
    private int mScreenWidth;
    private int mSkyHeight;
    private float mSunTopOffset;

    private VelocityTracker mVelocityTracker;

    private int offset;
    private float percent;

    private Sky mSky;
    private Sun mSun;
    private Building mBuilding;
    private AppBarLayout mAppBarLayout;
    private CoordinatorLayout mCoordinatorLayout;
    private Context mContext;
    public static final float DEFAULT_SUNRISE_TOP_PERCENT = 0.9f;
    private OnRefreshBegin mOnRefreshBegin;

    /**
     * 开始刷新接口，refreshBegin()方法中实现具体的刷新操作，当然注意耗时操作要在子线程中
     */
    public interface OnRefreshBegin{
        void refreshBegin();
    }


    public void setOnRefreshBegin(OnRefreshBegin onRefreshBegin){
        mOnRefreshBegin = onRefreshBegin;
    }

    public void setCoordinatorLayout(CoordinatorLayout coordinatorLayout){
        mCoordinatorLayout = coordinatorLayout;
        mAppBarLayout = (AppBarLayout)coordinatorLayout.findViewById(R.id.headview);

    }

    public void setIsReleaseDrag(boolean isReleaseDrag){
        this.mSun.setIsDragRelease(isReleaseDrag);
    }

    public boolean getCanTouch(){
       return mSun.getCanTouch();
   }

    public RentalsSunHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mVelocityTracker = VelocityTracker.obtain();
        initiateDimens();
        createBitmaps(context);
    }

    public RentalsSunHeaderView(Context context) {
        super(context);
        mContext = context;
        mVelocityTracker = VelocityTracker.obtain();
        initiateDimens();
        createBitmaps(context);
    }



    public RentalsSunHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initiateDimens();
        createBitmaps(context);
    }


    private void initiateDimens() {
        PtrLocalDisplay.init(mContext);
        int mTotalDefaultHeight = PtrLocalDisplay.dp2px(Constant.DEFAULT_HEADER_HEIGHT);
        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mSkyHeight = PtrLocalDisplay.dp2px(Constant.SKY_HEIGHT);
        mSunTopOffset = (mTotalDefaultHeight * DEFAULT_SUNRISE_TOP_PERCENT);
    }

    private void createBitmaps(Context context) {
        mSky = new Sky(context,this,mScreenWidth,mSkyHeight);
        mBuilding = new Building(context,this);
        mSun = new Sun(context,0,mSunTopOffset,mScreenWidth,this);
    }

    public void offsetTopAndBottom(int offset) {
        mSun.setCanvasTop(offset);
    }

    public void resetOriginals() {
        this.clearAnimation();
        setPercent(0);
        setIsReleaseDrag(false);
        mSun.reset();
        invalidate();
    }


    public void setOffset(int offset){
        mSun.setmOffset(offset);
        invalidate();
    }

    public void setPercent(float percent) {
        mSun.setDragPercent(percent);
        mSun.setRotate(percent);
        mBuilding.setDragPercent(percent);
        this.invalidate();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = mSkyHeight;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height + getPaddingTop() + getPaddingBottom(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int saveCount = canvas.save();
        mSky.draw(canvas);
        mSun.draw(canvas);
        mBuilding.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    public void refreshBegin() {
        setPercent(percent);
        invalidate();
    }

    public void refreshComplete() {
        mSun.refreshComplete();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        offset = PtrLocalDisplay.dp2px(Constant.TOTAL_DRAG_DISTANCE+Constant.DEFAULT_HEADER_HEIGHT)+i;
        if (offset <= PtrLocalDisplay.dp2px(Constant.DEFAULT_HEADER_HEIGHT)){
            resetOriginals();
        }
        setOffset(offset);
        percent = (offset-PtrLocalDisplay.dp2px(Constant.DEFAULT_HEADER_HEIGHT))/(PtrLocalDisplay.dp2px(100)*1.0f);
        setPercent(percent);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float touchY = 0;
        if (!getCanTouch()){
            return true;
        }
        mVelocityTracker.addMovement(event);
        mVelocityTracker.computeCurrentVelocity(1000);
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            setIsReleaseDrag(false);
            touchY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE){
            refreshBegin();
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
            setIsReleaseDrag(true);
            //第一阶段，回弹到顶
            if (offset<PtrLocalDisplay.dp2px(Constant.DEFAULT_HEADER_HEIGHT)){
                smoothTo(offset,mAppBarLayout);
            } else if (offset >= PtrLocalDisplay.dp2px(Constant.DEFAULT_HEADER_HEIGHT)&&offset<PtrLocalDisplay.dp2px(215)){
                //第二阶段，回弹到默认高度
                smoothTo(offset - PtrLocalDisplay.dp2px(Constant.DEFAULT_HEADER_HEIGHT),mAppBarLayout);
                Log.d("aaaad", "event.getY()" + event.getY() + " - touchY    " + touchY);
                if ((event.getY()- touchY)<0){

                    return true;
                }
            //第三阶段，执行刷新
            } else if (offset >= PtrLocalDisplay.dp2px(Constant.RETURN_TO_DEFAULT_HEIGHT)) {
                smoothTo(offset - PtrLocalDisplay.dp2px(Constant.RETURN_TO_DEFAULT_HEIGHT),mAppBarLayout);
                mOnRefreshBegin.refreshBegin();
            }
        }
        return false;
    }

    public void smoothTo(){
        smoothTo(offset - PtrLocalDisplay.dp2px(Constant.DEFAULT_HEADER_HEIGHT), mAppBarLayout);
    }
    public void smoothTo(int offset,View view){
        ViewCompat.postOnAnimation(view, new OffsetRunnable(offset, view));
    }

    private class OffsetRunnable implements Runnable{

        private int offset;//总偏移量
        private View view;//需要偏移的view
        private int i = 40;//每一次滚动的距离，可以控制速度
        public OffsetRunnable(int offset,View view){
            this.offset = offset;
            this.view = view;
        }
        @Override
        public void run() {
            if (offset >= i){
                setAppBarOffset(i);
                ViewCompat.postOnAnimation(view, new OffsetRunnable(offset-i,view));//递归
            } else if (offset > 0){
                setAppBarOffset(offset);
            }
        }
    }

    public void setAppBarOffset(int offsetPx){
        mCoordinatorLayout.dispatchDependentViewsChanged(mAppBarLayout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        behavior.onNestedPreScroll(mCoordinatorLayout, mAppBarLayout, null, 0, offsetPx, new int[]{0, 0});

    }

}
