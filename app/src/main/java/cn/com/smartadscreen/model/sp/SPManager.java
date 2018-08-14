package cn.com.smartadscreen.model.sp;


import android.os.Environment;

import com.blankj.utilcode.util.SPUtils;

import cn.com.smartadscreen.model.db.entity.PlayInfo;


/**
 * Created by Taro on 2017/3/15.
 * SharedPreferences 管理类
 */
public class SPManager {

    /**
     * data 目录的相对路径
     */
    public static final String RELATIVE_APP_DATA_PATH = "/startai/data/";

    private static SPUtils utils;

    private static final String SP_NAME = "SMART_AD_H5.config";

    // Const
    public static final String KEY_SDCARD_PATH = "KEY_SDCARD_PATH";
    public static final String KEY_APP_VERSION_CODE = "KEY_APP_VERSION_CODE";
    public static final String KEY_APP_WEB_FOLDER = "KEY_APP_WEB_FOLDER";
    public static final String KEY_APP_WWW_FOLDER = "KEY_APP_WWW_FOLDER";
    public static final String KEY_APP_LOAD_FILE_PATH = "KEY_APP_LOAD_FILE_PATH";
//    public static final String KEY_APP_DATA_FOLDER = "KEY_APP_DATA_FOLDER";
    public static final String KEY_DEVICE_SN = "KEY_DEVICE_SN";
    public static final String KEY_ENABLED_HARDWARE_KEY = "KEY_ENABLED_HARDWARE_KEY";
    public static final String KEY_ENABLED_TOAST_DOWNLOAD_INFO = "KEY_ENABLED_TOAST_DOWNLOAD_INFO";
    public static final String KEY_APP_USE_CROSSWALK = "KEY_APP_USE_CROSSWALK";
    public static final String KEY_ENABLED_TOUCH_EVENT = "KEY_ENABLED_TOUCH_EVENT";
    public static final String KEY_APP_WEBVIEW_LAYER_TYPE = "KEY_APP_WEBVIEW_LAYER_TYPE";
    public static final String KEY_ENABLED_SEND_SMARTAD_LOAD = "KEY_ENABLED_SEND_SMARTAD_LOAD";
    public static final String KEY_ENABLED_SEND_PLUGIN_MESSAGE = "KEY_ENABLED_SEND_PLUGIN_MESSAGE";
    public static final String KEY_ENABLED_RECEIVE_PLUGIN_MESSAGE = "KEY_ENABLED_RECEIVE_PLUGIN_MESSAGE";
    public static final String KEY_FIXED_VIEWPAGER_INDEX = "KEY_FIXED_VIEWPAGER_INDEX";
    /**
     * 当前播表
     */
    public static final String KEY_CURRENT_BT_ID = "KEY_CURRENT_BT_ID";
    /**
     * 当前播表开始播放的播放时间
     */
    public static final String KEY_CURRENT_BT_PLAY_TIME = "KEY_CURRENT_BT_PLAY_TIME";
    public static final String KEY_IS_MUSIC_BOX = "KEY_IS_MUSIC_BOX";
    public static final String KEY_STICK_TOP_BT_ID = "KEY_STICK_TOP_BT_ID";
    public static final String KEY_IGNORE_BT_ID = "KEY_IGNORE_BT_ID";
    public static final String KEY_SYNC_TYPE = "KEY_SYNC_TYPE";
    public static final String KEY_SCREEN_SHUT_OFF = "KEY_SCREEN_SHUT_OFF";
    /**
     * 设备当前变量
     */
    public static final String KEY_CURRENT_BATTERY = "KEY_CURRENT_BATTERY";
    /**
     * 设备电量类型
     */
    public static final String KEY_POWER_TYPE = "KEY_POWER_TYPE";
    //应用是否初始化完成
    public static final String KEY_IS_INIT_SUCCESS = "KEY_IS_INIT_SUCCESS";
    // 0 播放 1 暂停
    public static final String KEY_PROGRAM_STATE = "KEY_PROGRAM_STATE";
    /**
     * 本地ip
     */
    public static final String KEY_LOCAL_IP = "KEY_LOCAL_IP";


    //播放器相关====================================================================================
    //当前播放器模式
    public static final String KEY_CURRENT_PLAYER_MODE = "KEY_CURRENT_PLAYER_MODE";
    //当前循环模式
    public static final String KEY_CURRENT_LOOP_MODE = "KEY_CURRENT_LOOP_MODE";
    //当前播放的视频id
    public static final String KEY_CURRENT_VIDEO_PLAY_ID = "KEY_CURRENT_VIDEO_PLAY_ID";
    //当前播放视频的进度
    public static final String KEY_CURRENT_VIDEO_PLAY_PROGRESS = "KEY_CURRENT_VIDEO_PLAY_PROGRESS";

    //当前播放的音频id
    public static final String KEY_CURRENT_AUDIO_PLAY_ID = "KEY_CURRENT_AUDIO_PLAY_ID";
    //当前播放音频的进度
    public static final String KEY_CURRENT_AUDIO_PLAY_PROGRESS = "KEY_CURRENT_AUDIO_PLAY_PROGRESS";

    //当前播放的图片id
    public static final String KEY_CURRENT_IMAGE_PLAY_ID = "KEY_CURRENT_IMAGE_PLAY_ID";
    //当前播放图片的进度
    public static final String KEY_CURRENT_IMAGE_PLAY_PROGRESS = "KEY_CURRENT_IMAGE_PLAY_PROGRESS";

    public static SPUtils getManager() {
        return SPManager.getInstance().getSPUtils();
    }

    public SPUtils getSPUtils() {
        return utils;
    }

    private static final class InstanceHolder {
        private static final SPManager INSTANCE = new SPManager();
    }

    private SPManager() {
        utils = SPUtils.getInstance(SP_NAME);
    }

    public static SPManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public synchronized void saveCurrentBtId(long id) {
        utils.put(KEY_CURRENT_BT_ID, id);
    }

    public synchronized long getCurrentBtId() {
        return utils.getLong(KEY_CURRENT_BT_ID, -1L);
    }

    public synchronized void saveWebViewLayerType(int type) {
        utils.put(KEY_APP_WEBVIEW_LAYER_TYPE, type);
    }

    /**
     * 应用是否初始化完成
     * @param isInitSuccess 应用是否初始化完成
     */
    public synchronized void saveIsInitSuccess(boolean isInitSuccess) {
        utils.put(KEY_IS_INIT_SUCCESS, isInitSuccess);
    }
    public synchronized boolean isInitSuccess(
) {
        return utils.getBoolean(KEY_IS_INIT_SUCCESS, false);
    }

    /**
     * WebView加速类型
     *
     * @return
     */
    public synchronized int getWebViewLayerType() {
        return utils.getInt(KEY_APP_WEBVIEW_LAYER_TYPE, 1);
    }

    /**
     * 固定页
     *
     * @param index
     */
    public synchronized void saveFixedViewpagerIndex(int index) {
        utils.put(KEY_FIXED_VIEWPAGER_INDEX, index);
    }

    public synchronized int getFixedViewpagerIndex() {
        return utils.getInt(KEY_FIXED_VIEWPAGER_INDEX, -1);
    }

    /**
     * 同步方式
     *
     * @param type
     */
    public synchronized void saveSyncType(int type) {
        utils.put(KEY_SYNC_TYPE, type);
    }

    public synchronized int getSyncType() {
        return utils.getInt(KEY_SYNC_TYPE, 0);
    }

    /**
     * 允许SmartPlugin通过SmartAD自启动
     */
    public synchronized void saveEnabledSendSmartadLoad(boolean enable) {
        utils.put(KEY_ENABLED_SEND_SMARTAD_LOAD, enable);
    }

    public synchronized boolean getEnabledSendSmartadLoad() {
        return utils.getBoolean(KEY_ENABLED_SEND_SMARTAD_LOAD, true);
    }

    /**
     * 允许SmartAD向SmartPlugin转发消息
     *
     * @param enable
     */
    public synchronized void saveEnabledSendPluginMessage(boolean enable) {
        utils.put(KEY_ENABLED_SEND_PLUGIN_MESSAGE, enable);
    }

    public synchronized boolean getEnabledSendPluginMessage() {
        return utils.getBoolean(KEY_ENABLED_SEND_PLUGIN_MESSAGE, true);
    }

    /**
     * 允许SmartPlugin发送消息
     *
     * @param enable
     */
    public synchronized void saveEnabledReceivePluginMessage(boolean enable) {
        utils.put(KEY_ENABLED_RECEIVE_PLUGIN_MESSAGE, enable);
    }

    public synchronized boolean getEnabledReceivePluginMessage() {
        return utils.getBoolean(KEY_ENABLED_RECEIVE_PLUGIN_MESSAGE, true);
    }

    public synchronized void saveSdcardPath(String sdcardPath) {
        if (sdcardPath == null) {
            return;
        }
        utils.put(KEY_SDCARD_PATH, sdcardPath);
    }

    /**
     * 获取 sd卡路径
     *
     * @return sd卡路径
     */
    public synchronized String getSdcardPath() {
        return utils.getString(KEY_SDCARD_PATH, Environment.getExternalStorageDirectory().getPath());
    }

    /**
     * 获取 data的路径
     *
     * @return data目录的路径
     */
    public synchronized String getAppDataPath() {
        return getSdcardPath() + RELATIVE_APP_DATA_PATH;
    }

    /**
     * 保存本地ip
     * @param localIp 本地ip
     */
    public synchronized  void saveLocalIP(String localIp){
        utils.put(KEY_LOCAL_IP, localIp == null ? "" : localIp);
    }

    public synchronized String getLocalIP(){
        return utils.getString(KEY_LOCAL_IP);
    }

    /**
     * 是否为音响
     * @param isMusicBox 是否是音响
     */
    public synchronized void saveIsMusicBox(boolean isMusicBox){
        utils.put(KEY_IS_MUSIC_BOX, isMusicBox);
    }

    public synchronized boolean isMusicBox(){
        return utils.getBoolean(KEY_IS_MUSIC_BOX, false);
    }

    /**
     * 保存当前播放模式
     * @param playMode  PlayInfo.TYPE_CLOCK 时钟页面
     *                  PlayInfo.TYPE_VIDEO 视频播放页面
     *                  PlayInfo.TYPE_MUSIC 音乐播放页面
     *                  PlayInfo.TYPE_IMAGE 图片播放页面
     *                  PlayInfo.TYPE_AD    广告页面
     */
    public synchronized void saveCurrentPlayerMode(int playMode){
        utils.put(KEY_CURRENT_PLAYER_MODE, playMode);
    }



    public synchronized int getCurrentPlayerMode() {
        return utils.getInt(KEY_CURRENT_PLAYER_MODE, PlayInfo.TYPE_CLOCK);
    }

    /**
     * 保存当前循环模式
     */
    public synchronized void saveCurrentLoopMode(int playMode){
        utils.put(KEY_CURRENT_LOOP_MODE, playMode);
    }



    /**
     * 保存当前视频播放id
     */
    public synchronized void saveCurrentVideoPlayId(long id) {
        utils.put(KEY_CURRENT_VIDEO_PLAY_ID, id);
    }

    public synchronized long getCurrentVideoPlayId(){
        return utils.getLong(KEY_CURRENT_VIDEO_PLAY_ID, -1);
    }

    /**
     * 保存当前视频播放进度
     */
    public synchronized void saveCurrentVideoPlayProgress(int progress){
        utils.put(KEY_CURRENT_VIDEO_PLAY_PROGRESS, progress);
    }

    public synchronized int getCurrentVideoPlayProgress(){
        return utils.getInt(KEY_CURRENT_VIDEO_PLAY_PROGRESS, 0);
    }

    /**
     * 保存当前音频播放id
     */
    public synchronized void saveCurrentAudioPlayId(long id) {
        utils.put(KEY_CURRENT_AUDIO_PLAY_ID, id);
    }

    public synchronized long getCurrentAudioPlayId(){
        return utils.getLong(KEY_CURRENT_AUDIO_PLAY_ID, -1);
    }

    /**
     * 保存当前音频播放进度
     */
    public synchronized void saveCurrentAudioPlayProgress(int progress){
        utils.put(KEY_CURRENT_AUDIO_PLAY_PROGRESS, progress);
    }

    public synchronized int getCurrentAudioPlayProgress(){
        return utils.getInt(KEY_CURRENT_AUDIO_PLAY_PROGRESS, 0);
    }

    /**
     * 保存当前图片播放id
     */
    public synchronized void saveCurrentImagePlayId(long id) {
        utils.put(KEY_CURRENT_IMAGE_PLAY_ID, id);
    }

    public synchronized long getCurrentImagePlayId(){
        return utils.getLong(KEY_CURRENT_IMAGE_PLAY_ID, -1);
    }

    /**
     * 保存当前图片播放进度
     */
    public synchronized void saveCurrentImagePlayProgress(int progress){
        utils.put(KEY_CURRENT_IMAGE_PLAY_PROGRESS, progress);
    }

    public synchronized int getCurrentImagePlayProgress(){
        return utils.getInt(KEY_CURRENT_IMAGE_PLAY_PROGRESS, 0);
    }
}
