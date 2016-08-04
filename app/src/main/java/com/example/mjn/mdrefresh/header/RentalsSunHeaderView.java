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
import com.example.mjn.mdrefresh.RefreshListener;
import com.example.mjn.mdrefresh.RefreshUIListener;
import com.example.mjn.mdrefresh.UIListenerHolder;
import com.example.mjn.mdrefresh.utils.Constant;


public class RentalsSunHeaderView extends View implements View.OnTouchListener, AppBarLayout.OnOffsetChangedListener{
    private int mScreenWidth;
    private int mSkyHeight;
    private float mSunTopOffset;
    private UIListenerHolder mUIListenerHolder = new UIListenerHolder();

    private VelocityTracker mVelocityTracker;

    private int offset;

    private AppBarLayout mAppBarLayout;
    private CoordinatorLayout mCoordinatorLayout;
    private Context mContext;
    public static final float DEFAULT_SUNRISE_TOP_PERCENT = 0.9f;
    private RefreshListener mRefreshListener;
    private boolean mCanTouch;

    public void setRefreshUIListener(RefreshUIListener listener){
        mUIListenerHolder.addListener(listener);
    }

    public void setRefreshListener(RefreshListener refreshListener){
        mRefreshListener = refreshListener;
    }

    public void setCoordinatorLayout(CoordinatorLayout coordinatorLayout){
        mCoordinatorLayout = coordinatorLayout;
        mAppBarLayout = (AppBarLayout)coordinatorLayout.findViewById(R.id.headview);

    }

    public void setIsReleaseDrag(boolean isReleaseDrag){
        mUIListenerHolder.setIsReleaseDrag(isReleaseDrag);
    }

    public boolean getCanTouch(){
       return mCanTouch;
   }

    public RentalsSunHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mVelocityTracker = VelocityTracker.obtain();
        initiateDimens();
    }

    public RentalsSunHeaderView(Context context) {
        super(context);
        mContext = context;
        mVelocityTracker = VelocityTracker.obtain();
        initiateDimens();
    }



    public RentalsSunHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initiateDimens();
    }


    private void initiateDimens() {
        Constant.init(mContext);
        int mTotalDefaultHeight = Constant.dp2px(Constant.DEFAULT_HEADER_HEIGHT);
        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mSkyHeight = Constant.dp2px(Constant.SKY_HEIGHT);
        mSunTopOffset = (mTotalDefaultHeight * DEFAULT_SUNRISE_TOP_PERCENT);
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        mUIListenerHolder.offsetTopAndBottom(offset);
    }



    public void setOffset(int offset){
        mUIListenerHolder.setOffset(offset);
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
        mUIListenerHolder.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    public void refreshBegin() {
        mCanTouch = false;
        setOffset(offset);
        invalidate();
    }

    public void refreshComplete() {
        mCanTouch = true;
        mUIListenerHolder.refreshComplete();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        offset = Constant.dp2px(Constant.TOTAL_DRAG_DISTANCE+Constant.DEFAULT_HEADER_HEIGHT)+i;
        if (offset <= Constant.dp2px(Constant.DEFAULT_HEADER_HEIGHT)){
            mUIListenerHolder.resetOriginals();
        }
        setOffset(offset);
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
            if (offset<Constant.dp2px(Constant.DEFAULT_HEADER_HEIGHT)){
                smoothTo(offset,mAppBarLayout);
            } else if (offset<Constant.dp2px(Constant.RETURN_TO_DEFAULT_HEIGHT)){
                //第二阶段，回弹到默认高度
                smoothTo(offset - Constant.dp2px(Constant.DEFAULT_HEADER_HEIGHT),mAppBarLayout);
                Log.d("aaaad", "event.getY()" + event.getY() + " - touchY    " + touchY);
                if ((event.getY()- touchY)<0){
                    return true;
                }
            //第三阶段，执行刷新
            } else {
                smoothTo(offset - Constant.dp2px(Constant.RETURN_TO_DEFAULT_HEIGHT),mAppBarLayout);
                mRefreshListener.onRefreshStart();
            }
        }
        return false;
    }

    public void smoothTo(){
        smoothTo(offset - Constant.dp2px(Constant.DEFAULT_HEADER_HEIGHT), mAppBarLayout);
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
