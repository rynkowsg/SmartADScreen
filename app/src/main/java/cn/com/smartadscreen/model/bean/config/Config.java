package cn.com.smartadscreen.model.bean.config;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import cn.com.smartadscreen.app.Application;


/**
 * Created by Taro on 2017/3/15.
 * SmartADH5 全部配置类
 */
public class Config {

    /**
     * 播放模式
     */
    public static final int PLAY_MODE_ORDER = 1;//顺序

    public static final int PLAY_MODE_SINGLE = 2;//单曲循环

    public static final int PLAY_MODE_RANDOM = 3;//随机

    /**
     * 启动实体按键
     */
    public static final String ACTION_ENABLED_HARDWARE_KEY = "ACTION_ENABLED_HARDWARE_KEY";
    /**
     * 展示下载进度页面
     */
    public static final String ACTION_SHOW_DOWNLOAD_PROGRESS = "ACTION_SHOW_DOWNLOAD_PROGRESS";
    /**
     * 启用 Toast 下载进度
     */
    public static final String ACTION_ENABLED_TOAST_DOWNLOAD_INFO = "ACTION_ENABLED_TOAST_DOWNLOAD_INFO";
    /**
     * 是否启用 CrossWalk
     */
    public static final String ACTION_APP_USE_CROSSWALK = "ACTION_APP_USE_CROSSWALK";

    /**
     * 设置界面超时时间
     */
    public static final int SETTING_TIME_OUT = 3 * 60 * 1000;

    /**
     * 屏幕是否为关闭状态
     */
    public static boolean screenOff = false;

    /**
     * 当前电量
     */
    public static int curBattery = 0;



    /**
     * 电池类型，充电、未充电
     */
    public static boolean isBatteryCharging = false;

    private static Handler mMainHandler;

    public static void setup() {
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }

    }

    public static Context getContext() {
        return Application.getContext();
    }

    public static Handler getMainHandler() {
        return mMainHandler;
    }

}
