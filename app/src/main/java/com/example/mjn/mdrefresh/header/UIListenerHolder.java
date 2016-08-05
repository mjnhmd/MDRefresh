package com.example.mjn.mdrefresh.header;

import android.graphics.Canvas;

import com.example.mjn.mdrefresh.header.RefreshUIListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengjingnan on 16/7/29.
 */
public class UIListenerHolder implements RefreshUIListener {
    private List<RefreshUIListener> mList = new ArrayList<>();

    public void addListener(RefreshUIListener listener){
        if (listener == null){
            return;
        }
        if (mList.contains(listener)){
            return;
        }
        mList.add(listener);
    }

    @Override
    public void setOffset(int offset) {
        if (mList.size() <= 0){
            return;
        }
        for (RefreshUIListener listener:mList){
            listener.setOffset(offset);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (mList.size() <= 0){
            return;
        }
        for (RefreshUIListener listener:mList){
            listener.draw(canvas);
        }
    }

    @Override
    public void refreshComplete() {
        if (mList.size() <= 0){
            return;
        }
        for (RefreshUIListener listener:mList){
            listener.refreshComplete();
        }
    }

    @Override
    public void resetOriginals() {
        if (mList.size() <= 0){
            return;
        }
        for (RefreshUIListener listener:mList){
            setOffset(0);
            listener.resetOriginals();
        }
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        if (mList.size() <= 0){
            return;
        }
        for (RefreshUIListener listener:mList){
            listener.offsetTopAndBottom(offset);
        }
    }

    @Override
    public void setIsReleaseDrag(boolean isReleaseDrag) {
        if (mList.size() <= 0){
            return;
        }
        for (RefreshUIListener listener:mList){
            listener.setIsReleaseDrag(isReleaseDrag);
        }
    }
}
