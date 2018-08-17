package cn.com.smartadscreen.main.ui.web;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.xwalk.core.JavascriptInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;

import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.model.bean.DownloadTask;
import cn.com.smartadscreen.model.bean.OnTimeoutExecute;
import cn.com.smartadscreen.model.bean.OnVideoPlayer;
import cn.com.smartadscreen.model.bean.TaskPush;
import cn.com.smartadscreen.model.bean.event.OnTextPlayer;
import cn.com.smartadscreen.model.db.entity.BroadcastTable;
import cn.com.smartadscreen.model.db.manager.BroadcastTableHelper;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.manager.DownloadManager;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;
import cn.com.smartadscreen.utils.TimerUtils;


/**
 * Created by Taro on 2017/4/1.
 * XWalkView 的 JavaScriptInterface
 */
public class SmartXJsInterface {
    /**
     * JavaScript 模块准备完毕, 请求初始化
     */
    @JavascriptInterface
    public void requestConnect(){
        // 初始化请求
        DataSourceUpdateModule.doTaskPush(true);
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native", "HTML请求连接初始化"));
    }

    /**
     * Javascript 接收到消息推送, 返回消息
     * @param msg Json 序列化后的 字符串
     *            {
     *                  sid: sid,
     *                  datatype: "add"
     *            }
     */
    @JavascriptInterface
    public void dataExecute(String msg){
        JSONObject msgObject = JSON.parseObject(msg);
        ArrayList<String> remarks = new ArrayList<>();
        remarks.add("sid: " + msgObject.getString("sid"));
        remarks.add("datatype: " + msgObject.getString("datatype"));
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native", "HTML成功接收到推送指令", remarks));
    }
    /**
     * JavaScript 记录日志
     */
    @android.webkit.JavascriptInterface
    public void WriteLogByHTML(String key,String value){
        Logger.d("SmartJsInterface  HTML记录日志");
        ArrayList remarks = new ArrayList();
        remarks.add(key+":"+"  "+value);
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HTMLWRITE, "HTML", "Native", "html记录日志",remarks));
    }

    private Map<String, List<String>> timeoutMap = new HashMap<>();

    @JavascriptInterface
    public String setTimeout(String msg){
        String timeoutKey = UUID.randomUUID().toString();
        JSONObject timeoutJson = JSON.parseObject(msg);
        String sid = timeoutJson.getString("sid");
        if (timeoutMap.containsKey(sid)) {
            timeoutMap.get(sid).add(timeoutKey);
        } else {
            List<String> list = new ArrayList<>();
            list.add(timeoutKey);
            timeoutMap.put(sid, list);
        }

        TimerUtils.schedule(timeoutKey, new TimerTask() {
            @Override
            public void run() {
                TimerUtils.close(timeoutKey);
                EventBus.getDefault().post(new OnTimeoutExecute(timeoutKey));
            }
        }, timeoutJson.getIntValue("delay"));
        return timeoutKey;
    }

    /**
     * 取消定时器
     * @param sid sid 取消某一个sid对应的所有定时器
     */
    @JavascriptInterface
    public void clearTimeout(String sid){
        if (timeoutMap.containsKey(sid)) {
            List<String> list = timeoutMap.get(sid);
            for (String timeoutKey : list) {
                TimerUtils.close(timeoutKey);
            }
            timeoutMap.remove(sid);
        }
    }

    @JavascriptInterface
    public boolean existsFile(String filePath) {
        return FileUtils.isFileExists(filePath);
    }

    @JavascriptInterface
    public boolean existsFileOrDownload(String filePath, String hash, String file) {
        boolean fileExists = FileUtils.isFileExists(filePath);
        if (!fileExists) {
            // 文件不存在
            Logger.i("文件不存在!");
            if (hash != null && file != null) {
                Logger.i("H5验证文件不存在, 执行下载!");
                String downloadKey = DownloadManager.getDownloadKey();
                DownloadManager.addDownloadTask(downloadKey,
                        new DownloadTask(file, filePath, "不存在文件", hash));
                DownloadManager.startTask(downloadKey, "");
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native", "H5验证文件不存在, 执行下载!"));
            }
        }
        return fileExists;
    }

    @JavascriptInterface
    public String playVideo(String videoInfo) {
        String callBackKey = UUID.randomUUID().toString();
        OnVideoPlayer onVideoPlayer = JSON.parseObject(videoInfo, OnVideoPlayer.class);

        int dpX = onVideoPlayer.getX();
        int dpY = onVideoPlayer.getY();
        int dpWidth = onVideoPlayer.getWidth();
        int dpHeight = onVideoPlayer.getHeight();

        onVideoPlayer.setX(ConvertUtils.dp2px(dpX));
        onVideoPlayer.setY(ConvertUtils.dp2px(dpY));
        onVideoPlayer.setWidth(ConvertUtils.dp2px(dpWidth));
        onVideoPlayer.setHeight(ConvertUtils.dp2px(dpHeight));
        ArrayList<String> remarks = new ArrayList<>();
        remarks.add("dpX:" + dpX);
        remarks.add("dpY:" + dpY);
        remarks.add("dpWidth:" + dpWidth);
        remarks.add("dpHeight:" + dpHeight);
        remarks.add("videoInfo:" + videoInfo);
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native", "播放视频参数", remarks));
        onVideoPlayer.setCallbackKey(callBackKey);
        EventBus.getDefault().post(onVideoPlayer);
        return callBackKey;
    }

    @JavascriptInterface
    public void cancelVideo(String videoId) {
        OnVideoPlayer cancelVideoPlayer = new OnVideoPlayer();
        cancelVideoPlayer.setVideoId(videoId);
        cancelVideoPlayer.setCancelIntent(true);
        EventBus.getDefault().post(cancelVideoPlayer);
    }

    @JavascriptInterface
    public String playText(String textInfo) {
        String callBackKey = UUID.randomUUID().toString();
        OnTextPlayer onTextPlayer = JSON.parseObject(textInfo, OnTextPlayer.class);
        Log.i("WebFragment"," playText smartxJS "+onTextPlayer.getSize());
        int dpX = onTextPlayer.getX();
        int dpY = onTextPlayer.getY();
        int dpWidth = onTextPlayer.getWidth();
        int dpHeight = onTextPlayer.getHeight();

        onTextPlayer.setX(ConvertUtils.dp2px(dpX));
        onTextPlayer.setY(ConvertUtils.dp2px(dpY));
        onTextPlayer.setWidth(ConvertUtils.dp2px(dpWidth));
        onTextPlayer.setHeight(ConvertUtils.dp2px(dpHeight));
        ArrayList<String> remarks = new ArrayList<>();
        remarks.add("dpX:" + dpX);
        remarks.add("dpY:" + dpY);
        remarks.add("dpWidth:" + dpWidth);
        remarks.add("dpHeight:" + dpHeight);
        remarks.add("textInfo:" + textInfo);
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native", "跑马灯参数", remarks));
        onTextPlayer.setCallbackKey(callBackKey);
        EventBus.getDefault().post(onTextPlayer);
        return callBackKey;
    }

    @JavascriptInterface
    public void cancelText(String textId) {
        OnTextPlayer onTextPlayer = new OnTextPlayer();
        onTextPlayer.setTextId(textId);
        onTextPlayer.setCancelIntent(true);
        EventBus.getDefault().post(onTextPlayer);
    }

    /**
     * 播表过时
     */
    @JavascriptInterface
    public void tableOutOfData(){
        long currentId = SPManager.getManager().getLong(SPManager.KEY_CURRENT_BT_ID, -1L);
        BroadcastTableHelper helper = BroadcastTableHelper.getInstance();
        if(currentId != -1L) {
            helper.deleteById(currentId);
        }
        BroadcastTable nextTable = helper.queryPreBroadcastTableById(helper.queryLastId() + 1);
        if(nextTable != null) {
            DataSourceUpdateModule.playBtById(String.valueOf(nextTable.getId()));
        } else {
            SPManager.getInstance().saveCurrentBtId(-1L);

            //发送空白信息
            JSONObject nothingJson = new JSONObject();
            nothingJson.put("datatype", "nothing EmptyPage");
            TaskPush taskPush = new TaskPush(nothingJson.toString());
            EventBus.getDefault().post(taskPush);
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native", "播表已经过时了"));
        }
    }
}
