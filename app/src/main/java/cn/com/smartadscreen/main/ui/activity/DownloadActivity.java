package cn.com.smartadscreen.main.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import cn.com.smartadscreen.app.Application;
import cn.com.smartadscreen.main.ui.adapter.DownloadListAdapter;
import cn.com.smartadscreen.main.ui.base.BaseActivityV1;
import cn.com.smartadscreen.model.bean.DownloadProgressBean;
import cn.com.smartadscreen.model.db.entity.DownloadTable;
import cn.com.smartadscreen.model.db.manager.DownloadTableHelper;
import cn.com.smartadscreen.utils.TimerUtils;
import cn.com.startai.smartadh5.R;

import static cn.com.smartadscreen.model.bean.config.Config.SETTING_TIME_OUT;


/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/17
 * 作用：
 */

public class DownloadActivity extends BaseActivityV1 {

    private final int HANDLER_WHAT_QUERY_DOWNLOAD_LIST = 0x10;
    private final int HANDLER_WHAT_UPDATE_PROGRESS = 0x11;

    @BindView(R.id.rv_download)
    RecyclerView rvDownload;
    @BindView(R.id.tv_no_content)
    TextView tvNoContent;

    private List<DownloadTable> mAllDownloads;
    private DownloadListAdapter adapter;
    private boolean mInitSuccess;

    //销毁当前 Activity 的任务的key
    private final String KEY_FINISH_TASK = "DownloadActivity.task.finish";

    private HandlerThread handlerThread;
    private Handler mHandler;


    public static void showActivity(Activity context) {
        Intent intent = new Intent(context, DownloadActivity.class);
        context.startActivity(intent);
    }

    public static void showActivityForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, DownloadActivity.class);
        context.startActivityForResult(intent, requestCode);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_download;
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        mAllDownloads = new ArrayList<>();
        initHandler();
    }

    @Override
    public void initView() {
        setNoContentShow();
        adapter = new DownloadListAdapter(this, mAllDownloads);
        rvDownload.setLayoutManager(new LinearLayoutManager(this));
        rvDownload.setAdapter(adapter);

        mHandler.sendEmptyMessage(HANDLER_WHAT_QUERY_DOWNLOAD_LIST);
    }

    private void initHandler(){
        handlerThread = new HandlerThread("[DownloadActivity.handlerThread]", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLER_WHAT_QUERY_DOWNLOAD_LIST:
                        List<DownloadTable> downloadTables = DownloadTableHelper.getInstance().queryDownloadList();
                        for (DownloadTable downloadTable : downloadTables) {
                            if (downloadTable.getProgress() < 100 && downloadTable.getFilePath() != null) {
                                File file = new File(downloadTable.getFilePath());
                                if (!TextUtils.isEmpty(downloadTable.getSize())) {
                                    int realProgress = (int) Math.ceil((file.length()  / downloadTable.getFileSizeDouble()) * 100);
                                    Logger.d(downloadTable.getFileName() + "realProgress:" + realProgress);
                                    if (realProgress >= 99) {//如果文件下载完成，就把进度修改为100
                                        downloadTable.setProgress(100);
                                        DownloadTableHelper.getInstance().update(downloadTable);
                                    }
                                }
                            }
                        }
                        mAllDownloads.clear();
                        mAllDownloads.addAll(DownloadTableHelper.getInstance().queryDownloadList());
                        runOnUiThread(() -> {

                            setNoContentShow();
                            adapter.notifyDataSetChanged();
                        });
                        break;
                    case HANDLER_WHAT_UPDATE_PROGRESS:
                        List<DownloadTable> downloadTables2 = DownloadTableHelper.getInstance().queryDownloadList();
                        mAllDownloads.clear();
                        mAllDownloads.addAll(downloadTables2);
                        runOnUiThread(() -> {
                            setNoContentShow();
                            adapter.notifyDataSetChanged();
                        });
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void setNoContentShow() {
        if (mAllDownloads == null || mAllDownloads.isEmpty()) {
            tvNoContent.setVisibility(View.VISIBLE);
        } else {
            tvNoContent.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void addViewListener() {

    }

    @Override
    protected boolean shouldInterceptBack() {
        return false;
    }

    @Subscribe()
    public void onDownload(DownloadProgressBean bean) {
        mHandler.sendEmptyMessage(HANDLER_WHAT_UPDATE_PROGRESS);
//        try {
//            if (mInitSuccess) {
//                mAllDownloads.clear();
//                mAllDownloads.addAll(DownloadTableHelper.getInstance().querylast20AndErrorData());
//                runOnUiThread(() -> {
//                    tvNoContent.setVisibility(mAllDownloads.isEmpty() ? View.VISIBLE : View.GONE);
//                    adapter.notifyDataSetChanged();
//                });
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //开启定时任务，TIME_OUT 时间后关闭当前界面
        TimerUtils.schedule(KEY_FINISH_TASK, new FinishActivityTask(), SETTING_TIME_OUT);
        Logger.i("设置页面定时任务开启");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //关闭定时任务
        TimerUtils.close(KEY_FINISH_TASK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        handlerThread.quit();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        handlerThread = null;
        //purge方法用于从这个计时器(Timer) 的任务队列中移除所有已取消的任务。
        Timer timer = TimerUtils.getTimerByKey(KEY_FINISH_TASK);
        if (timer != null) {
            timer.purge();
        }
        //检测泄露
        Application.getContext().mRefWatcher.watch(this);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //有按键操作时重新启动定时任务
        if (event.getAction() == KeyEvent.ACTION_DOWN)
            TimerUtils.schedule(KEY_FINISH_TASK, new FinishActivityTask(), SETTING_TIME_OUT);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //有触摸操作时重新启动定时任务
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            TimerUtils.schedule(KEY_FINISH_TASK, new FinishActivityTask(), SETTING_TIME_OUT);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * finish当前页面的任务
     */
    private class FinishActivityTask extends TimerTask {
        public void run() {
            //该界面自动关闭时，将打开此界面的设置界面同时关闭
            runOnUiThread(() -> {
                setResult(RESULT_OK);
                DownloadActivity.this.finish();
            });
        }
    }
}
