package com.example.wangxiang.diffutilexample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxiang on 2016-10-10.
 * 对recyclerview进行刷新操作,其中对handler进行了防止内存泄露的处理
 */

public class MainActivity extends AppCompatActivity {
    private List<JavaBean> mDatas;
    private RecyclerView mRv;
    private DiffAdapter mAdapter;
    private static MyHandler mHandler;
    private static final int H_CODE_UPDATE = 1;
    private List<JavaBean> mNewDatas;//增加一个变量暂存newList

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DiffAdapter(this, mDatas);
        mRv.setAdapter(mAdapter);
        mHandler = new MyHandler(new WeakReference<>(this));
    }

    public static class MyHandler extends Handler {
        private WeakReference<MainActivity> weakReference;

        public MyHandler(WeakReference weak) {
            super();
            this.weakReference = weak;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity mainActy = weakReference.get();
            switch (msg.what) {
                case H_CODE_UPDATE:
                    //取出Result
                    DiffUtil.DiffResult diffResult = (DiffUtil.DiffResult) msg.obj;
                    //利用DiffUtil.DiffResult对象的dispatchUpdatesTo（）方法，传入RecyclerView的Adapter
                    diffResult.dispatchUpdatesTo(mainActy.mAdapter);
                    //别忘了将新数据给Adapter
                    mainActy.mDatas = mainActy.mNewDatas;
                    mainActy.mAdapter.setDatas(mainActy.mDatas);
                    break;
            }
        }
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mDatas.add(new JavaBean("wx1", "Android", R.drawable.pic1));
        mDatas.add(new JavaBean("wx2", "Java", R.drawable.pic2));
        mDatas.add(new JavaBean("wx3", "c", R.drawable.pic3));
        mDatas.add(new JavaBean("wx4", "php", R.drawable.pic4));
        mDatas.add(new JavaBean("wx5", "Go", R.drawable.pic5));
    }

    /**
     * 模拟刷新操作
     *
     * @param view
     */
    public void onRefresh(View view) {
        try {
            mNewDatas = new ArrayList<>();
            for (JavaBean bean : mDatas) {
                mNewDatas.add(bean.clone());//clone一遍旧数据 ，模拟刷新操作
            }
            mNewDatas.add(new JavaBean("新Item", "Got", R.drawable.pic6));//模拟新增数据
            mNewDatas.get(0).setDesc("Android+");
            mNewDatas.get(0).setPic(R.drawable.pic7);//模拟修改数据
            JavaBean testBean = mNewDatas.get(1);//模拟数据位移
            mNewDatas.remove(testBean);
            mNewDatas.add(testBean);

            //利用DiffUtil.calculateDiff()方法，传入一个规则DiffUtil.Callback对象，和是否检测移动item的 boolean变量，得到DiffUtil.DiffResult 的对象
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //放在子线程中计算DiffResult
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallBack(mDatas, mNewDatas), true);
                    Message message = mHandler.obtainMessage(H_CODE_UPDATE); //消息池减少开销
                    message.obj = diffResult;//obj存放DiffResult
                    message.sendToTarget(); //发送到对应的handler
                }
            }).start();
            //mAdapter.notifyDataSetChanged();//拒绝无脑刷新

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }


}
