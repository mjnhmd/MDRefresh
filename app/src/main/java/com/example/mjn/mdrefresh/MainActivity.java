package com.example.mjn.mdrefresh;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.example.mjn.mdrefresh.header.RentalsSunHeaderView;
import com.example.mjn.mdrefresh.header.view.Building;
import com.example.mjn.mdrefresh.header.view.Sun;
import com.example.mjn.mdrefresh.utils.Constant;

public class MainActivity extends AppCompatActivity implements IMainView{
    private RentalsSunHeaderView sunHeaderView;
    private AppBarLayout mAppBarLayout;//标题
    private int mTotalDragHeight;
    private MainPresenter mPresenter;
    private boolean mCanTouch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPresenter = new MainPresenter(this,this);
        initView();
    }

    private void initView() {
        Constant.init(this);
        CoordinatorLayout mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlyuout);
        sunHeaderView = (RentalsSunHeaderView) findViewById(R.id.sunheaderview);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.headview);
        sunHeaderView.setCoordinatorLayout(mCoordinatorLayout);
        RecyclerView mHomeListView = (RecyclerView) findViewById(R.id.home_layout_listview);
        mTotalDragHeight = Constant.dp2px(405) - Constant.dp2px(Constant.DEFAULT_HEADER_HEIGHT);
        sunHeaderView.setRefreshListener(mPresenter);
        //让view滚动到默认位置
        mAppBarLayout.post(new Runnable() {
            @Override
            public void run() {
                sunHeaderView.setAppBarOffset(mTotalDragHeight);
            }
        });
        if (mHomeListView != null) {
            //初始化内容列表
            mHomeListView.setLayoutManager(new LinearLayoutManager(this));
            mHomeListView.setAdapter(new HomeRecyclerAdapter(this, mPresenter.getRandomSublist(30)));
            mHomeListView.setOnTouchListener(sunHeaderView);
        }
        sunHeaderView.setRefreshUIListener(new Sun(this,sunHeaderView));
        sunHeaderView.setRefreshUIListener(new Building(this,sunHeaderView));
        mAppBarLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE){
                    return true;
                }
                return false;
            }
        });

    }


    @Override
    public void onPause() {
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(sunHeaderView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(sunHeaderView);
    }

    @Override
    public void refreshComplete() {
        sunHeaderView.refreshComplete();
    }
}

