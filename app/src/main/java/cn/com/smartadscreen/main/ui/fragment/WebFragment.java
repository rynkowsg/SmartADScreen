package cn.com.smartadscreen.main.ui.fragment;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.SPUtils;
import com.facebook.stetho.common.LogUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import cn.com.smartadscreen.app.Application;
import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.main.ui.base.BaseFragment;
import cn.com.smartadscreen.main.ui.view.AutoScrollMarqueeView;
import cn.com.smartadscreen.main.ui.view.EmptyFullVideoView;
import cn.com.smartadscreen.main.ui.view.SmartToast;
import cn.com.smartadscreen.main.ui.view.StatusBar;
import cn.com.smartadscreen.main.ui.web.SmartWebView;
import cn.com.smartadscreen.main.ui.web.SmartXWalkView;
import cn.com.smartadscreen.model.bean.OnVideoPlayer;
import cn.com.smartadscreen.model.bean.StatusBarBean;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.bean.event.OnTextPlayer;
import cn.com.smartadscreen.model.db.entity.PlayInfo;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.task.StateDate;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;
import cn.com.smartadscreen.utils.NmcUtils;
import cn.com.smartadscreen.utils.TimerUtils;
import cn.com.smartadscreen.utils.UriUtil;
import cn.com.startai.smartadh5.R;

public class WebFragment extends BaseFragment {
    private SmartWebView mSmartWebView;
    private SmartXWalkView mSmartXWalkView;
    // WebView 是否已经加载 url
    private boolean isLoaded = false;
    private boolean appUseCrossWalk;

    private RelativeLayout parentView;

    private StatusBar statusBar;

    private Map<String, OnVideoPlayer> videoViewMap = new HashMap<>();
    private Map<String, OnTextPlayer> textViewMap = new HashMap<>();
    private FrameLayout.LayoutParams mVideoParams;

    private String TAG_WEB = "web";

    private Handler mhandler = new Handler();
    private int maxTime = 1000 * 60 * 3;
    private int minTime = 1000;
    private String TAG = "WebFragment";


    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return parentView = (RelativeLayout) inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        RelativeLayout.LayoutParams webViewParams
                = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        appUseCrossWalk = SPManager.getManager().getBoolean(SPManager.KEY_APP_USE_CROSSWALK, false);

        String appLoadPath = SPManager.getManager().getString(SPManager.KEY_APP_LOAD_FILE_PATH);
        if (appUseCrossWalk) {
            if (mSmartXWalkView != null) {
                mSmartXWalkView.onDestroy();
                mSmartXWalkView = null;
            }
            mSmartXWalkView = SmartXWalkView.getInstance(this.getActivity());
            mSmartXWalkView.load("file://" + SPManager.getInstance().getSdcardPath() + appLoadPath, null);
        } else {
            if (mSmartWebView != null) {
                mSmartWebView.destroy();
                mSmartWebView = null;
            }
            mSmartWebView = SmartWebView.getInstance();
            mSmartWebView.loadUrl("file://" + SPManager.getInstance().getSdcardPath() + appLoadPath);
            Logger.i("init...");
        }

        int fixedPage = SPManager.getManager().getInt(SPManager.KEY_FIXED_VIEWPAGER_INDEX, 0);
        if (fixedPage == 1) {
            isLoaded = true;
        }
        if (appUseCrossWalk) {
            mSmartXWalkView.setLayoutParams(webViewParams);
            removeParent(mSmartXWalkView);
            parentView.addView(mSmartXWalkView);
        } else {
            mSmartWebView.setLayoutParams(webViewParams);
            removeParent(mSmartWebView);
            parentView.addView(mSmartWebView);
            //log
            Logger.i("已经加载Web页面");
        }
        initTextClock();
        Logger.i("init....");

    }


    @Override
    public void addViewListener() {

    }


    private void removeParent(View view) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeAllViewsInLayout();
            }
        }
    }

    /**
     * 实例化时钟控件
     */

    private void initTextClock() {
        statusBar = new StatusBar(getContext());
        if (statusBar == null) {
            Logger.i("statusBar...null");
        } else {
            Logger.i("statusBar" + statusBar);
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        statusBar.setLayoutParams(layoutParams);
        parentView.addView(statusBar);
    }

    /**
     * 操作时间控件
     *
     * @param bean isTimeShow 是否显示
     */
    public void operateTimeView(StatusBarBean bean) {
        String log;//日志信息
        String timeType = bean.getTimeType();
        boolean showTime = bean.isTimeShow();
        if (showTime) {
            log = "显示时间! ";
            if (Build.VERSION.SDK_INT == 17) {
                if (timeType.contains("HH")) {
                    timeType = timeType.replace("HH", "hh");
                    timeType += " a";
                }
            }
            if (!timeType.equals(statusBar.getTimeFormat24Hour())) {
                statusBar.setTimeFormat24Hour(timeType);
            }
        } else {
            log = "隐藏时间! ";
        }
        Logger.i(log);
        Logger.i("showtime" + showTime);
        statusBar.setTimeShow(showTime);
    }

    /**
     * 操作 logo 图片
     */
    public void operateLogo(StatusBarBean bean) {
        String log;//日志信息
        boolean isShowLogo = bean.isLogoShow();
        if (isShowLogo) {
            log = "显示Logo! ";
            statusBar.setLogoUri(bean.getLogoPath());
        } else {
            log = "隐藏Logo! ";
        }
        Logger.i(log);
        statusBar.setLogoShow(isShowLogo);
    }

    /**
     * 将 statusBar 组件放到布局最上层
     */
    private void bringTimeToFront() {
        if (statusBar.getVisibility() == View.VISIBLE) {
            statusBar.bringToFront();
        }
    }

    public void onTaskPush(String message) {

        LogUtil.d("live", "  webFragment onTaskPush() message: " + message);

        if (message.contains("'")) {
            message = message.replaceAll("'", " ");
        }
        String loadJavascript =
                "TMC.dataUpdate('" + message + "');";
        //log
        Logger.i("message:" + message);
        if (appUseCrossWalk) {
            if (mSmartXWalkView != null) {
                mSmartXWalkView.clearCache(true);
                mSmartXWalkView.load(SmartXWalkView.LOAD_JS_PREFIX.replace("$method", loadJavascript), null);
            }
        } else {
            if (mSmartWebView != null) {
                mSmartWebView.clearCache(true);
                mSmartWebView.loadUrl(SmartWebView.LOAD_JS_PREFIX.replace("$method", loadJavascript));
                Logger.i("H5已经执行加载");
            }
        }
    }

    /**
     * 播放视频
     **/
    public void onPlayVideo(OnVideoPlayer onVideoPlayer) {

        LogUtil.v(TAG_WEB, " onPlayVideo  curMillis:" + System.currentTimeMillis());
        //使用了GXY播放
        if (videoViewMap.containsKey(onVideoPlayer.getVideoId())
                && onVideoPlayer.getCurrentPosition() == 0) {
            EmptyFullVideoView videoView =
                    (EmptyFullVideoView) videoViewMap.get(onVideoPlayer.getVideoId()).getView()
                            .findViewById(R.id.id_video_view);
            onVideoPlayer.setCurrentPosition(videoView.getPlayPosition());
            onCancelVideo(onVideoPlayer.getVideoId(), true);
        }


        FrameLayout videoParent = new FrameLayout(this.getActivity());
        RelativeLayout.LayoutParams parentParams =
                new RelativeLayout.LayoutParams(onVideoPlayer.getWidth(), onVideoPlayer.getHeight());

        parentParams.setMargins(onVideoPlayer.getX(), onVideoPlayer.getY(), 0, 0);


        videoParent.setLayoutParams(parentParams);
        videoParent.setFocusable(false);

        EmptyFullVideoView videoView = new EmptyFullVideoView(this.getActivity());
        initVideoParams();

        videoView.setLayoutParams(mVideoParams);
        videoView.setId(R.id.id_video_view);
        //设置视频路径
        videoView.setUp(UriUtil.parseUrl(onVideoPlayer.getPath()), false, "");
        String videoPath = onVideoPlayer.getPath();
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "HTML", "Native", "视频设置的路径：" + videoPath));
        if (onVideoPlayer.getCurrentPosition() > 0 &&
                !(onVideoPlayer.getPath().startsWith("http") || onVideoPlayer.getPath().startsWith("rtmp"))) {
            videoView.setSeekOnStart(onVideoPlayer.getCurrentPosition());
//            videoView.seekTo();
        }

        LogUtil.v(TAG_WEB, " videoView.startPlayLogic();  curMillis:" + System.currentTimeMillis());
        videoView.startPlayLogic();
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native", "开始播放"));
        //写入播放状态
        String playState = Integer.toString(videoView.getCurrentState());
        ArrayList<String> remarks = new ArrayList<>();
        remarks.add("playState: " + playState);
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native", "播放状态：正常- 0,准备中- 1,播放中- 2,开始缓冲- 3,暂停- 4,错误状态- 7 ", remarks));

        videoParent.addView(videoView);
        parentView.addView(videoParent);
        onVideoPlayer.setView(videoParent);

        videoViewMap.put(onVideoPlayer.getVideoId(), onVideoPlayer);

//        videoView.start();

        videoView.setOnVideoPlayCompleteListener(new EmptyFullVideoView.OnVideoPlayCompleteListener() {
            @Override
            public void onComplete() {
                //播放完成
                String key = onVideoPlayer.getCallbackKey();
                LogUtil.v(TAG_WEB, " videoView onComplete key:" + key + " curMillis:" + System.currentTimeMillis());
                onCancelVideo(onVideoPlayer.getVideoId(), true);
                onVideoPlayerComplete(key, null);
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native", "播放完成"));
            }
        });

        if (videoView.getTop() <= statusBar.getBottom()) {
            bringTimeToFront();
        }
    }

    public void onVideoPlayerComplete(String videoKey, String err) {
        onTimeout(videoKey, err);
    }

    public void onTextPlayerComplete(String textKey, String err) {
        onTimeout(textKey, err);
    }

    public void onTimeout(String timeoutKey, String err) {
        String loadJavascript;
        if (err != null) {
            loadJavascript =
                    "TMC['" + timeoutKey + "']('" + timeoutKey + "', '" + err + "');";
        } else {
            loadJavascript =
                    "TMC['" + timeoutKey + "']('" + timeoutKey + "');";
        }

        if (appUseCrossWalk) {
            mSmartXWalkView.load(SmartXWalkView.LOAD_JS_PREFIX.replace("$method", loadJavascript), null);
        } else {
            mSmartWebView.loadUrl(SmartWebView.LOAD_JS_PREFIX.replace("$method", loadJavascript));
        }
    }

    public void onTimeout(String timeoutKey) {
        onTimeout(timeoutKey, null);
    }

    public void reload() {
        String appLoadPath = SPManager.getManager().getString(SPManager.KEY_APP_LOAD_FILE_PATH);
        if (appUseCrossWalk) {
            mSmartXWalkView.clearCache(true);
            mSmartXWalkView.load("file:///sdcard/" + appLoadPath, null);
        } else {
            mSmartWebView.clearCache(true);
            mSmartWebView.loadUrl("file:///sdcard/" + appLoadPath);
        }
    }

    /**
     * 实例化播放视频的 VideoView 的 LayoutParams
     */
    private void initVideoParams() {
        mVideoParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        mVideoParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        mVideoParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//        mVideoParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//        mVideoParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//        mVideoParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
    }

    /**
     * 撤销视频
     **/
    public void onCancelVideo(String videoId, boolean isRemove) {
        LogUtil.v(TAG_WEB, " onCancelVideo ");
        if (videoViewMap.containsKey(videoId)) {
            View view = videoViewMap.get(videoId).getView();
            EmptyFullVideoView videoView = (EmptyFullVideoView) view.findViewById(R.id.id_video_view);
//            if (videoView.play) {
//                videoView.stopPlayback();
//            }
            LogUtil.v(TAG_WEB, " videoView release " + videoId);
            //释放资源
            videoView.release();
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native", "停止播放,已经释放资源"));
            //写入文件
            String playState = Integer.toString(videoView.getCurrentState());
            ArrayList<String> remarks = new ArrayList<>();
            remarks.add("playState: " + playState);
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native", "播放状态：正常- 0,准备中- 1,播放中- 2,开始缓冲- 3,暂停- 4,错误状态- 7 ", remarks));

            parentView.removeView(view);
            if (isRemove) {
                videoViewMap.remove(videoId);
            }
        } else {

            LogUtil.v(TAG_WEB, " !videoViewMap.containsKey(videoId)  " + videoId);

            List<String> removeList = new ArrayList<>();
            for (Map.Entry<String, OnVideoPlayer> entry : videoViewMap.entrySet()) {
                if (entry.getKey().startsWith(videoId)) {
                    LogUtil.v(TAG_WEB, " removeList add  " + entry.getKey());
                    removeList.add(entry.getKey());
                }
            }
            for (String removeVideoId : removeList) {
                onCancelVideo(removeVideoId, true);
            }
        }
    }

    @Override
    public void onVisible() {
        super.onVisible();
        LogUtil.d("IndexActivity", "WebFragment onVisible");

        if (SPManager.getInstance().isMusicBox()) {
            SPManager.getInstance().saveCurrentPlayerMode(PlayInfo.TYPE_AD);
            NmcUtils.sendToNmcPlayerStatusChanged(-1, 0, 0);
        }
        if (getVisible()) {
//            sendState(1);
            EventBus.getDefault().post(new StateDate(1));
        }
        if (appUseCrossWalk) {
            if (mSmartXWalkView != null && getVisible()) {
                mSmartXWalkView.onShow();
                mSmartXWalkView.resumeTimers();
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "Native", "HTML", "已经执行onVisible"));
                if (isLoaded) {
                    continueVideo();
                }
            }
            if (!isLoaded && mSmartXWalkView != null) {
                isLoaded = true;
            }
        } else {
            if (mSmartWebView != null && getVisible()) {
                mSmartWebView.onResume();
                mSmartWebView.resumeTimers();
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "Native", "HTML", "已经执行onVisible"));

                if (isLoaded) {
                    continueVideo();
                }
            }
            if (!isLoaded && mSmartWebView != null) {
                isLoaded = true;
            }
        }
    }

    private void continueVideo() {

        String loadJavascript =
                "!function(){" +
                        "   var videos = document.getElementsByTagName('video');" +
                        "   for (var i = 0; i < videos.length; i++) {" +
                        "       videos[i].play();" +
                        "   }" +
                        "}();";
        if (appUseCrossWalk) {
            Logger.i("执行CrossWalk continue video!");
            mSmartXWalkView.load(SmartXWalkView.LOAD_JS_PREFIX.replace("$method", loadJavascript), null);
        } else {
            Logger.i("执行WebView continue video!");
            mSmartWebView.loadUrl(SmartWebView.LOAD_JS_PREFIX.replace("$method", loadJavascript));
        }

        // 执行原生的 continueVideo

        if (videoViewMap.size() > 0) {
            Logger.i("执行 VideoView continue video!");
            Map<String, OnVideoPlayer> videoViewMapCache = new HashMap<>();
            videoViewMapCache.putAll(videoViewMap);
            for (Map.Entry<String, OnVideoPlayer> entry : videoViewMapCache.entrySet()) {
                OnVideoPlayer videoPlayer = entry.getValue();
                //TODO H5播放视频的方法
                onPlayVideo(videoPlayer);
            }

            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native"
                    , "执行 continueVideo 中的 Video方法"));
        }

        // 执行原生的 continueText
        if (textViewMap.size() > 0) {
            Map<String, OnTextPlayer> textViewMapCache = new HashMap<>();
            textViewMapCache.putAll(textViewMap);
            for (Map.Entry<String, OnTextPlayer> entry : textViewMapCache.entrySet()) {
                OnTextPlayer onTextPlayer = entry.getValue();
                onPlayText(onTextPlayer);
            }
        }
    }

    /**
     * 开启跑马灯
     **/
    public void onPlayText(OnTextPlayer onTextPlayer) {
        Logger.i("onPlayText===>" + onTextPlayer.toString());

        if (textViewMap.containsKey(onTextPlayer.getTextId())) {
            onCancelText(onTextPlayer.getTextId(), true);
        }

        RelativeLayout.LayoutParams marqueeParams = new RelativeLayout.LayoutParams(onTextPlayer.getWidth(), onTextPlayer.getHeight());
        marqueeParams.setMargins(onTextPlayer.getX(), onTextPlayer.getY(), 0, 0);
        AutoScrollMarqueeView marqueeView = new AutoScrollMarqueeView(getContext());
        marqueeView.setLayoutParams(marqueeParams);
        marqueeView.setTextColor(onTextPlayer.getColor());
        marqueeView.setBackgroundColor(Color.parseColor(onTextPlayer.getBackgroundColor()));
        marqueeView.setSpeed(onTextPlayer.getSpeed());
        marqueeView.setTextSize(onTextPlayer.getSize());
        marqueeView.setText(Html.fromHtml(onTextPlayer.getContent()).toString());
        Log.i("WebFragment", "textSize=" + onTextPlayer.getSize() + "textSpeed= " + onTextPlayer.getSpeed());
        marqueeView.requestFocus();


        Logger.d("llllll 添加前==>" + parentView.getChildCount());

        if (getVisible()) {
            parentView.addView(marqueeView);
        }

        Logger.d("llllll 添加后==>" + parentView.getChildCount());

        onTextPlayer.setView(marqueeView);
        textViewMap.put(onTextPlayer.getTextId(), onTextPlayer);
        Config.getMainHandler().postDelayed(() -> {
            if (textViewMap.containsKey(onTextPlayer.getCallbackKey())) {
                onTextPlayerComplete(onTextPlayer.getCallbackKey(), null);
            }
        }, onTextPlayer.getLen() * 1000);

        if (marqueeView.getTop() <= statusBar.getBottom()) {
            bringTimeToFront();
        }
    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        LogUtil.d("IndexActivity", "WebFragment onInVisible");
//        sendState(2);
        EventBus.getDefault().post(new StateDate(2));
        pauseVideo();
        if (appUseCrossWalk) {
            if (mSmartXWalkView != null) {
                mSmartXWalkView.onHide();
                mSmartXWalkView.pauseTimers();
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "Native", "HTML", "已经执行onInVisible,释放webview所有资源"));
            }
        } else {
            if (mSmartWebView != null) {
                mSmartWebView.onPause();
                mSmartWebView.pauseTimers();
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "Native", "HTML", "已经执行onInVisible,释放webview所有资源"));

            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.i("WebFragment onPause");
        TimerUtils.schedule(TAG, new TimerTask() {
            @Override
            public void run() {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.v(TAG, "onPause 得sendstate执行了");
//                        sendState(2);
                        EventBus.getDefault().post(new StateDate(2));
                        LogUtil.v(TAG, "onPause 得sendstate执行了");
                    }
                });
            }
        }, 60 * 1000);
//        sendState(2);
        pauseVideo();
        if (appUseCrossWalk) {
            if (mSmartXWalkView != null && getVisible()) {
                mSmartXWalkView.onHide();
                mSmartXWalkView.pauseTimers();
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "Native", "HTML", "已经执行onPause,释放webview所有资源"));
            }
        } else {
            if (mSmartWebView != null && getVisible()) {
                mSmartWebView.onPause();
                mSmartWebView.pauseTimers();
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "Native", "HTML", "已经执行onPause,释放webview所有资源"));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SPManager.getInstance().isMusicBox()
                && SPManager.getInstance().getCurrentPlayerMode() != PlayInfo.TYPE_AD) {
            return;
        }
        if (getVisible()) {
            TimerUtils.schedule(TAG, new TimerTask() {
                @Override
                public void run() {
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.v(TAG, "onResume 得sendstate执行了");
//                            sendState(1);
                            EventBus.getDefault().post(new StateDate(1));
                            LogUtil.v(TAG, "onResume 得sendstate执行了");
                        }
                    });

                }
            }, 60 * 1000);
//            sendState(1);
        }
        if (appUseCrossWalk) {
            if (mSmartXWalkView != null && getVisible()) {
                mSmartXWalkView.onShow();
                mSmartXWalkView.resumeTimers();
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "Native", "HTML", "已经执行onResume,开启播放"));
                if (isLoaded) {
                    continueVideo();
                }
            }
        } else {
            if (mSmartWebView != null && getVisible()) {
                mSmartWebView.onResume();
                mSmartWebView.resumeTimers();
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "Native", "HTML", "已经执行onResume,开启播放"));
                if (isLoaded) {
                    continueVideo();
                }
            }
        }
        Logger.i("WebFragment onResume");

    }

    private void pauseVideo() {
        // 暂停原生的 video
        if (videoViewMap.size() > 0) {
            Logger.i("执行 VideoView pause video!");
            for (Map.Entry<String, OnVideoPlayer> entry : videoViewMap.entrySet()) {
                OnVideoPlayer videoPlayer = entry.getValue();
                EmptyFullVideoView videoView = (EmptyFullVideoView) videoPlayer.getView().findViewById(R.id.id_video_view);
                int currentPosition = videoView.getCurrentTime();
                videoPlayer.setCurrentPosition(currentPosition);
                onCancelVideo(videoPlayer.getVideoId(), false);
            }
        }

        if (textViewMap.size() > 0) {
            Logger.i("执行 TextView pause text!");
            for (Map.Entry<String, OnTextPlayer> entry : textViewMap.entrySet()) {
                OnTextPlayer onTextPlayer = entry.getValue();
                onCancelText(onTextPlayer.getTextId(), false);
            }
        }
    }

    /**
     * 撤销跑马灯
     **/
    public void onCancelText(String textId, boolean isRemove) {

        if (textViewMap.containsKey(textId)) {
            View view = textViewMap.get(textId).getView();
            parentView.removeView(view);
            if (isRemove) {
                textViewMap.remove(textId);
            }
        } else {
            List<String> removeList = new ArrayList<>();
            for (Map.Entry<String, OnTextPlayer> entry : textViewMap.entrySet()) {
                if (entry.getKey().startsWith(textId)) {
                    removeList.add(entry.getKey());
                }
            }
            for (String removeTextId : removeList) {
                onCancelText(removeTextId, true);
            }
        }
    }


    @Override
    public void onRightPress() {
        super.onRightPress();
        // 右键切换下一个播表
        SmartToast.info("切换下一个播表");
        DataSourceUpdateModule.doTaskPush(false, true, DataSourceUpdateModule.ACTION_SWITCH_BT_RIGHT);
    }

    @Override
    public void onLeftPress() {
        super.onLeftPress();
        // 左键切换上一个播表
        SmartToast.info("切换上一个播表");
        DataSourceUpdateModule.doTaskPush(false, true, DataSourceUpdateModule.ACTION_SWITCH_BT_LEFT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (appUseCrossWalk) {
            if (mSmartXWalkView != null) {
                mSmartXWalkView.stopLoading();
                mSmartXWalkView.onDestroy();
                mSmartXWalkView = null;
            }
        } else {
            if (mSmartWebView != null) {
                mSmartWebView.stopLoading();
                mSmartWebView.destroy();
                mSmartWebView = null;
            }
        }

        mhandler.removeCallbacksAndMessages(null);
        SPUtils.getInstance().put("NextNumber", -1);
        //泄露检测
        Application.getContext().mRefWatcher.watch(this);
    }
}
