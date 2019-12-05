package cn.com.smartadscreen.presenter.main;

import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.facebook.stetho.common.LogUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimerTask;

import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.main.ui.activity.IndexActivity;
import cn.com.smartadscreen.main.ui.contract.IndexContract;
import cn.com.smartadscreen.main.ui.base.BaseFragment;
import cn.com.smartadscreen.main.ui.fragment.WebFragment;
import cn.com.smartadscreen.model.bean.OnVideoPlayer;
import cn.com.smartadscreen.model.bean.StatusBarBean;
import cn.com.smartadscreen.model.bean.TaskPush;
import cn.com.smartadscreen.model.bean.config.Config;

import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.task.HourSyncTask;
import cn.com.smartadscreen.presenter.task.StateDate;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;
import cn.com.smartadscreen.utils.SmartTimerService;
import cn.com.smartadscreen.utils.TimerUtils;
import cn.startai.apkcommunicate.CommunicateType;
import cn.startai.apkcommunicate.StartaiCommunicate;


import static cn.com.smartadscreen.app.Application.getContext;

@SuppressWarnings("all")
public class IndexPresenter implements IndexContract.Presenter {
    public IndexActivity view;
    private WebFragment mWebFragment;
    private List<BaseFragment> mFragments;
    private final String TAG = "IndexPresenter";
    private int maxTime = 1000 * 60 * 3;
    private int minTime = 1000;
    private String TAG_WEB = "web";


    public IndexPresenter(IndexActivity view, WebFragment mWebFragment, List<BaseFragment> mFragments) {
        this.view = view;
        this.mWebFragment = mWebFragment;
        this.mFragments = mFragments;
        EventBus.getDefault().register(this);
    }


    public void unRegister() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void doTaskPushByPre(TaskPush taskPush) {
        int index = mFragments.indexOf(mWebFragment);
        if (!Config.screenOff && view.getCurrentItem() != index) {
            //跳转到web界面
                view.setCurrentItem(index);
        }
        // 有向 WebFragment 推送的播表消息
        if (mWebFragment != null) {
            // record
            LogMsg logMsg = new LogMsg(LogMsg.TYPE_SEND, "Native", "HTML", "向前端推送播表消息");
            // record remarks
            ArrayList<String> remarks = new ArrayList<>();
            StringBuilder remark = new StringBuilder();
            // 延迟推送的消息
            if (taskPush.isDelay()) {

                LogUtil.d("live", "  indexActivity delayToPush() ");

                remark.append("是否延迟推送: 是\n");
                remark.append("当前屏幕状态：").append(Config.screenOff ? "关屏" : "开屏").append("\n");
                remark.append("延迟推送时间: ");
                remark.append(TimeUtils.date2String(taskPush.getDate())).append("\n");
                remark.append("\n推送消息: ");
                remark.append(taskPush.getMessage());
                remarks.add(remark.toString());
                delayToPushByPre(taskPush);
//                delayToPush(taskPush);

            } else {

                LogUtil.d("live", "  indexActivity pushToWeb() ");

                remark.append("是否延迟推送: 否\n");
                remark.append("当前屏幕状态：").append(Config.screenOff ? "关屏" : "开屏");
                remark.append("\n推送消息: ");
                remark.append(taskPush.getMessage());
                remarks.add(remark.toString());
                Logger.i("doTaskPush的pushToWeb执行");
//                pushToWeb(taskPush);
                Logger.i("taskPush"+taskPush);
                pushToWebByPre(taskPush);
            }

            logMsg.setRemarks(remarks);
            SmartLocalLog.writeLog(logMsg);
        }

    }

    /**
     * 将播表 Screen 推送到 Web 中
     */
    @Override
    public void pushToWebByPre(TaskPush taskPush) {
        if (taskPush.isAdd()) {
            long currId = SPManager.getInstance().getCurrentBtId();
            if (currId != taskPush.getTableId()) {
                SPManager.getInstance().saveCurrentBtId(taskPush.getTableId());
                SPManager.getManager().put(SPManager.KEY_CURRENT_BT_PLAY_TIME, System.currentTimeMillis());
            }
        }
//        Logger.i("屏幕开关:"+Config.screenOff);
        if (!Config.screenOff) {
            // 推送到 WebView 页面
            JSONObject messageObj = JSON.parseObject(taskPush.getMessage());
            if (messageObj.getString("datatype").equals("nothing EmptyPage")) {
                //没有播表
                sendState(4, false);
                mWebFragment.onTaskPush(taskPush.getMessage());//todo 修改引用
                Logger.i("没有播表...");
                return;
            }
            if (checkSpots(messageObj)) {
                //播放
                sendState(1, false);
                mWebFragment.onTaskPush(taskPush.getMessage());//todo 修改引用
                Logger.i("taskPush"+taskPush.getTimeType()+" / "+taskPush.getLogoPath());
                operateStatusBar(taskPush.getTimeType(), taskPush.getLogoPath());
                Logger.i("播表播放,推送的消息：" + taskPush.getMessage());
            }
        }

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendState(StateDate stateDate) {
        this.sendState(stateDate.getState(), true);
    }

    /**
     * @param state          1-resume 播放 2-pause 暂停 3 停止
     * @param isNeedSendToH5 是否需要向h5发送状态
     */
    public void sendState(int state, boolean isNeedSendToH5) {
        Handler mHandler = new Handler();
        if (isNeedSendToH5) {
            // state 0-播表为空 1-resume 播放 2-pause 暂停 3 停止
            JSONObject stateJson = new JSONObject();
            String stateString = state == 1 ? "resume" : "pause";
            stateJson.put("datatype", stateString);
            mWebFragment.onTaskPush(stateJson.toJSONString());
            Logger.i("发送状态:" + stateString);
        }
        int bool = SPUtils.getInstance().getInt("NextNumber", -1);
        LogUtil.v("WebFragment", "bool= " + bool);
        if (bool == 1) {//判断是否右键返回
            // 准备向NMC发送程序状态
            //复原bool
            SPUtils.getInstance().put("NextNumber", -1);
            if (state != 4) {
                JSONObject btContent = DataSourceUpdateModule.getBtDetailContent(DataSourceUpdateModule.getCurrentBt());
                if (btContent != null) {
                    btContent.put("status", state);
                    StartaiCommunicate.getInstance().send(getContext(),
                            CommunicateType.COMMUNICATE_TYPE_PLAYLIST_CHANGE,
                            btContent.toString());
                }
            } else {
                //播表为空
                JSONObject nullJson = new JSONObject();
                nullJson.put("id", "");
                nullJson.put("name", "");
                nullJson.put("screens", new ArrayList<>());
                nullJson.put("time", 0L);
                nullJson.put("status", 4);
                StartaiCommunicate.getInstance().send(getContext(),
                        CommunicateType.COMMUNICATE_TYPE_PLAYLIST_CHANGE,
                        nullJson.toString());

            }
        } else {
            Random random = new Random();
            ArrayList<String> remarks = new ArrayList<>();
            int postDelayTime = random.nextInt(maxTime) % (maxTime - minTime + 1) + minTime;
            if (state != 4) {
                JSONObject btContent = DataSourceUpdateModule.getBtDetailContent(DataSourceUpdateModule.getCurrentBt());
                if (btContent != null) {
                    btContent.put("status", state);
                    TimerUtils.schedule(TAG, new TimerTask() {
                        @Override
                        public void run() {
                            StartaiCommunicate.getInstance().send(getContext(),
                                    CommunicateType.COMMUNICATE_TYPE_PLAYLIST_CHANGE,
                                    btContent.toString());
                            LogUtil.v(TAG, "延迟发送时间 postDelayTime=" + postDelayTime);
                            LogUtil.v(TAG, "播放状态 state= " + state);
                            LogUtil.v(TAG, "按键返回 bool= " + bool);
                            remarks.add("state: " + state);
                            remarks.add("postDelayTime: " + postDelayTime);
                            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "Native", "HTML", "回传播放状态以及延迟时间给云端", remarks));
                        }
                    }, postDelayTime);
                }
            } else {
                //播表为空
                JSONObject nullJson = new JSONObject();
                nullJson.put("id", "");
                nullJson.put("name", "");
                nullJson.put("screens", new ArrayList<>());
                nullJson.put("time", 0L);
                nullJson.put("status", 4);
                StartaiCommunicate.getInstance().send(getContext(),
                        CommunicateType.COMMUNICATE_TYPE_PLAYLIST_CHANGE,
                        nullJson.toString());
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native"
                        , "播表为空"));
            }

        }
        // 存储
        SPManager.getManager().put(SPManager.KEY_PROGRAM_STATE, state);
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native"
                , "广告屏的播放状态 state ==" + state));
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native"
                , "执行 sendState 方法"));

        if (state == 1 && IndexActivity.isBtStop() && !Config.screenOff) {
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native"
                    , "执行 sendState 方法，重推播表"));
            DataSourceUpdateModule.playBtById("-1");
            IndexActivity.setBtStop(false);
        }
    }


    private StatusBarBean mStatusBarBean;// 用来操作状态栏的的logo和时间的实体类，此处用来避免重复调用方法

    /**
     * 操作状态栏
     */
    private void operateStatusBar(String timeType, String logoPth) {

        StatusBarBean sbean = new StatusBarBean(timeType, logoPth);
        if (!sbean.equals(mStatusBarBean)) {
           boolean b= mWebFragment == null? true:false;
           Logger.i("mWebFragment"+b);
           Logger.i("sbean"+sbean);
            mWebFragment.operateTimeView(sbean);
            mWebFragment.operateLogo(sbean);
        }
        mStatusBarBean = sbean;

    }

    private boolean checkSpots(JSONObject messageObj) {
        // 检测播表是否是插播播表, 返回true不阻止推送到html, 返回false阻止
        Logger.i("推送Screen -> html: " + messageObj);

        JSONObject screen = messageObj.getJSONObject("screen");

        if (screen != null) {
            String sid = screen.getString("sid");
            String utype = screen.getString("utype");
            if (sid.equals("sid_0") && utype.equals("2")) {
                Logger.e("播表属于插播");
                boolean isMusicBox = SPManager.getInstance().isMusicBox();
                if (isMusicBox) {
                    // 音箱

                } else {
                    // 其他

                }
                return false;
            } else if (utype.equals("2")) {
                return false;
            }

            if (!SPManager.getInstance().isMusicBox()) {
                int syncType = SPManager.getInstance().getSyncType();
                if (syncType == 0) {
                    // 每小时同步
                    Integer totalLength = screen.getInteger("totalLength"); // 总时间
                    int timeInterval = totalLength == null ? 1 : totalLength / 3600 + 1; // 时间间隔
                    String schedulingPattern = "0 */" + timeInterval + " * * *";
                    SmartTimerService.getInstance().startTask("每小时同步", schedulingPattern,
                            //每小时同步任务
                            new HourSyncTask(timeInterval, view));

                } else {
//                    // 绝对时间同步, 计算时间线
//                    Integer totalLength = screen.getInteger("totalLength");
//                    if (totalLength != null) {
//                        long start = screen.getLongValue("start");
//                        countTimeLineToPushTask(totalLength, start);
//                    }
                    //不同步
                }
            }
        }

        return true;
    }


    @Override
    public void delayToPushByPre(TaskPush taskPush) {
        SmartTimerService.getInstance().startTask(taskPush.getScreenId(), taskPush.getDate(), () -> {
            if (System.currentTimeMillis() > taskPush.getDate().getTime()) {
                view.runOnUiThread(() ->
                        pushToWebByPre(taskPush)
                );
                //当任务执行完成后停止任务
                SmartTimerService.getInstance().stopTask(taskPush.getScreenId());
            }
        });
        //todo 注意
    }

    @Override
    public void checkSpotsByPre(JSONObject messageObj) {

    }

    @Override
    public void doWhileTaskPushByPre(long startTime, long splitLength) {
// 记录日志
        ArrayList<String> remarks = new ArrayList<>();
        remarks.add("下一个自动刷新时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(startTime)));
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native", "绝对时间同步开启", remarks));

        SmartTimerService.getInstance().startTask("绝对时间同步", new Date(startTime), () -> {
            view.runOnUiThread(() -> {
                DataSourceUpdateModule.doTaskPush(false);
                // 执行任务结束后, 执行下一个时间点
                doWhileTaskPushByPre(startTime + splitLength, splitLength);
            });
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoPalyer(OnVideoPlayer onVideoPlayer) {
        ArrayList<String> remark = new ArrayList<>();
        if (onVideoPlayer.getCancelIntent() != null && onVideoPlayer.getCancelIntent()) {
            // cancel video
            Logger.i("H5请求原生取消播放视频! videoId: " + onVideoPlayer.getVideoId() + " path: " + onVideoPlayer.getPath());
            remark.add("屏幕状态：" + (Config.screenOff ? "关屏" : "开屏"));
            remark.add("取消播放视频!");

            if (mWebFragment != null) {
                mWebFragment.onCancelVideo(onVideoPlayer.getVideoId(), true);
            }
        } else {

            remark.add("屏幕状态：" + (Config.screenOff ? "关屏" : "开屏"));
            remark.add("path: " + onVideoPlayer.getPath());

            if (!Config.screenOff) {
                remark.add("播放视频! ");
                Logger.i("H5请求原生播放视频! videoId: " + onVideoPlayer.getVideoId());
                if (mWebFragment != null) {
                    mWebFragment.onPlayVideo(onVideoPlayer);
                }
            } else {
                remark.add("屏幕关闭，不播放视频 ");
            }
        }

        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native"
                , "H5请求原生视频操作!", remark));
    }


}
