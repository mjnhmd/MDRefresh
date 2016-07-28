package com.example.mjn.mdrefresh;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.mjn.mdrefresh.utils.Cheeses;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by mengjingnan on 16/7/25.
 *
 */
public class MainPresenter implements RefreshListener{
    IMainView mMainView;
    Context mContext;

    public MainPresenter(Context context,IMainView mainView){
        mMainView = mainView;
        mContext = context;
    }
    /**
     * 随机生成内容数据，这里用的cheesesquare的方法
     */
    public List<String> getRandomSublist(int amount) {
        ArrayList<String> list = new ArrayList<>(amount);
        Random random = new Random();
        while (list.size() < amount) {
            list.add(Cheeses.sCheeseStrings[random.nextInt(Cheeses.sCheeseStrings.length)]);
        }
        return list;
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mMainView.refreshComplete();
            Toast.makeText(mContext, "刷新完成", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshStart() {
        Log.d("下拉刷新请求数据", "");
        Toast.makeText(mContext, "刷新开始", Toast.LENGTH_SHORT).show();
        new RefreshTask().execute();
    }

}
