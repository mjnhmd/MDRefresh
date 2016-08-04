package com.example.mjn.mdrefresh;

import android.graphics.Canvas;

/**
 * Created by mengjingnan on 16/7/29.
 */
public interface RefreshUIListener {
    void setOffset(int offset);
    void draw(Canvas canvas);
    void refreshComplete();
    void resetOriginals();
    void offsetTopAndBottom(int offset);
    void setIsReleaseDrag(boolean isReleaseDrag);
}
