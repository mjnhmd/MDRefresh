package com.example.mjn.mdrefresh;

import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.mjn.mdrefresh.header.RentalsSunHeaderView;
import com.example.mjn.mdrefresh.utils.Cheeses;
import com.example.mjn.mdrefresh.utils.Constant;
import com.example.mjn.mdrefresh.utils.PtrLocalDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private RentalsSunHeaderView sunHeaderView;
    private AppBarLayout mAppBarLayout;//标题
    private int mTotalDragHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        CoordinatorLayout mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlyuout);
        sunHeaderView = (RentalsSunHeaderView) findViewById(R.id.sunheaderview);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.headview);
        sunHeaderView.setCoordinatorLayout(mCoordinatorLayout);
        RecyclerView mHomeListView = (RecyclerView) findViewById(R.id.home_layout_listview);
        mTotalDragHeight = PtrLocalDisplay.dp2px(405) - PtrLocalDisplay.dp2px(Constant.DEFAULT_HEADER_HEIGHT);

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
            mHomeListView.setAdapter(new HomeRecyclerAdapter(this, getRandomSublist(Cheeses.sCheeseStrings, 30)));
            mHomeListView.setOnTouchListener(sunHeaderView);
        }

        //监听开始刷新
        sunHeaderView.setOnRefreshBegin(new RentalsSunHeaderView.OnRefreshBegin() {
            @Override
            public void refreshBegin() {
                Log.d("下拉刷新请求数据", "");
                Toast.makeText(MainActivity.this, "刷新开始", Toast.LENGTH_SHORT).show();
                new RefreshTask().execute();
            }
        });

    }

    /**
     * 模拟刷新操作，在这里执行数据刷新
     */
    class RefreshTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sunHeaderView.refreshComplete();
            Toast.makeText(MainActivity.this, "刷新完成", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 随机生成内容数据，这里抄的cheesesquare的方法
     */
    private List<String> getRandomSublist(String[] array, int amount) {
        ArrayList<String> list = new ArrayList<>(amount);
        Random random = new Random();
        while (list.size() < amount) {
            list.add(array[random.nextInt(array.length)]);
        }
        return list;
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

}

