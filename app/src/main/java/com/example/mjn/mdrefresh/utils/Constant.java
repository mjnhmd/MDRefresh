package com.example.mjn.mdrefresh.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Administrator on 2015/10/8.
 *
 */
public class Constant {
    public static float SCREEN_DENSITY;

    public static void init(Context context) {
        if (context == null) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        SCREEN_DENSITY = dm.density;
    }

    public static int dp2px(float dp) {
        final float scale = SCREEN_DENSITY;
        return (int) (dp * scale + 0.5f);
    }

    public static final int RELEASE_ANIMATION_DURATION = 500 ;  //释放后Scroller恢复到初始位置的时长
    public static final int REFRESH_DURATION = 1400;    //在顶部刷新的时间
    public static final int SUN_DOWN_ANIMATION_DURATION = RELEASE_ANIMATION_DURATION - 100;  //太阳下山的时间
    public static final int ANIMATION_TIMEOUT_DURATION = 4000;

    public static final int TOTAL_DRAG_DISTANCE = 290;   //拖拽距离
    public static final int DEFAULT_HEADER_HEIGHT = 115;   //默认header高度
    public static final int RETURN_TO_DEFAULT_HEIGHT = 215;//松手后返回到默认高度的距离
    public static final int SKY_HEIGHT = TOTAL_DRAG_DISTANCE + DEFAULT_HEADER_HEIGHT;  //天空的总高度


}
