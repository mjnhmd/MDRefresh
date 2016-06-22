package com.example.mjn.mdrefresh.utils;

/**
 * Created by Administrator on 2015/10/8.
 *
 */
public class Constant {
    public static final int RELEASE_ANIMATION_DURATION = 500 ;  //释放后Scroller恢复到初始位置的时长
    public static final int REFRESH_DURATION = 1400;    //在顶部刷新的时间
    public static final int SUN_DOWN_ANIMATION_DURATION = RELEASE_ANIMATION_DURATION - 100;  //太阳下山的时间
    public static final int ANIMATION_TIMEOUT_DURATION = 3000;

    public static final int TOTAL_DRAG_DISTANCE = 290;   //拖拽距离
    public static final int DEFAULT_HEADER_HEIGHT = 115;   //默认header高度
    public static final int RETURN_TO_DEFAULT_HEIGHT = 215;//松手后返回到默认高度的距离
    public static final int SKY_HEIGHT = TOTAL_DRAG_DISTANCE + DEFAULT_HEADER_HEIGHT;  //天空的总高度

}
