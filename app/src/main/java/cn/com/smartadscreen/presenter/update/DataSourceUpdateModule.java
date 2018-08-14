package cn.com.smartadscreen.presenter.update;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.facebook.stetho.common.LogUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.model.bean.ContentItemBean;
import cn.com.smartadscreen.model.bean.DownloadFinished;
import cn.com.smartadscreen.model.bean.ReportMsg;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.db.entity.App;
import cn.com.smartadscreen.model.db.entity.BroadcastTable;
import cn.com.smartadscreen.model.db.entity.FileMapping;
import cn.com.smartadscreen.model.db.entity.Screen;
import cn.com.smartadscreen.model.db.entity.Service;
import cn.com.smartadscreen.model.db.gen.BroadcastTableDao;
import cn.com.smartadscreen.model.db.manager.BroadcastTableHelper;
import cn.com.smartadscreen.model.db.manager.DBManager;
import cn.com.smartadscreen.model.db.manager.ServiceHelper;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.audio.AudioPlayer;
import cn.com.smartadscreen.presenter.audio.IjkMusicPlayer;
import cn.com.smartadscreen.presenter.manager.DownloadManager;
import cn.com.smartadscreen.presenter.service.ClearDataIntentService;
import cn.com.smartadscreen.presenter.service.DataUpdateIntentService;
import cn.com.smartadscreen.presenter.service.FileUpdateIntentService;
import cn.com.smartadscreen.presenter.service.SendHistoryBtIntentServer;
import cn.com.smartadscreen.presenter.service.SendNeedReplaceFileIntentService;
import cn.com.smartadscreen.presenter.service.TaskPushIntentService;
import cn.com.smartadscreen.presenter.service.UniqueDataSourceFileIntentService;
import cn.com.smartadscreen.utils.JSONUtils;
import cn.com.smartadscreen.utils.SmartToast;


/**
 * Created by Taro on 2017/3/13.
 * Data Source Update 模块主入口
 */
public class DataSourceUpdateModule {

    public static final String TAG = "DataSourceUpdateModule";

    public static final String EXTRA_UPDATE_OBJ = "EXTRA_UPDATE_OBJ";
    public static final String EXTRA_PUSH_FOR_INIT = "EXTRA_PUSH_FOR_INIT";
    public static final String EXTRA_IS_SWITCH_BT = "EXTRA_IS_SWITCH_BT";
    public static final String EXTRA_SWITCH_BT_ACTION = "EXTRA_SWITCH_BT_ACTION";
    public static final String EXTRA_IS_PLAY_ACTION = "EXTRA_IS_PLAY_ACTION";

    public static final String ACTION_SWITCH_BT_LEFT = "ACTION_SWITCH_BT_LEFT";
    public static final String ACTION_SWITCH_BT_RIGHT = "ACTION_SWITCH_BT_RIGHT";

    private static DataSourceUpdateModule instance;

    private DataSourceUpdateModule(){}

    private static DataSourceUpdateModule newInstance(){
        if (instance == null) {
            instance = new DataSourceUpdateModule();
            if (!EventBus.getDefault().isRegistered(instance)) {
                EventBus.getDefault().register(instance);
            }
        }
        return instance;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDownloadFinished(DownloadFinished download){
        if (download.getTag().equals(TAG)) {
            BroadcastTable bt = DBManager.getDaoSession().getBroadcastTableDao()
                    .queryBuilder()
                    .where(BroadcastTableDao.Properties.DownloadKey.eq(download.getDownloadKey())).unique();
            if (bt != null) {
                bt.setFinished(true);
                bt.update();
                String message = bt.getName() + " 播表下载完毕";
                // record
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native", message));
                SmartToast.success(message);

                Logger.i(" onDownloadFinished  doReplaceBroadcastTable :" + bt.getId());

                LogUtil.d("live", " onDownloadFinished  start SendNeedReplaceFileIntentService : "+ bt.getId() );
                doReplaceBroadcastTable(bt.getId());

                long curBtId = SPManager.getInstance().getCurrentBtId();
                BroadcastTable curBt = BroadcastTableHelper.getInstance().queryById(curBtId);
                //如果，当前播放的播表的修改时间大于 已下载完成播表的修改时间，不再推送展示当前下载完成的播表
                if(curBt != null && curBt.getModifyDate().getTime() > bt.getModifyDate().getTime()) {
                    return;
                }
                doTaskPush(false);



                // TODO: 2017/6/16  对短消息进行处理, 短消息不执行推送

            }
        }
    }

    public static void doReplaceBroadcastTable(long id) {
        Context context = Config.getContext();
        Intent intent = new Intent(context, SendNeedReplaceFileIntentService.class);
        intent.putExtra("id", id);
        context.startService(intent);
    }

    public static void doReplaceBroadcastTable(String content) {
        Context context = Config.getContext();
        Intent intent = new Intent(context, SendNeedReplaceFileIntentService.class);
        intent.putExtra("content", content);
        context.startService(intent);
    }

    public static void doDataUpdate(JSONObject msgObject){
        doDataUpdate(msgObject, false);
    }

    public static void doDataUpdate(JSONObject msgObject, boolean mute){
        if (!mute) {
            SmartToast.info("收到一条播表消息!");
            //播放声音
            AudioPlayer.playHandleBroadcastMessageAudio();

        }

        Context context = Config.getContext();
        Intent intent = new Intent(context, DataUpdateIntentService.class);
        intent.putExtra(EXTRA_UPDATE_OBJ, msgObject.toString());
        context.startService(intent);

        if (instance == null) {
            DataSourceUpdateModule.newInstance();
        }
    }

    // 执行任务分发
    public static void doTaskPush(boolean pushForInit){
        doTaskPush(pushForInit, false, null);

    }

    public static void doTaskPush(boolean pushForInit, boolean isSwitch, String action) {
        Context context = Config.getContext();
        Intent intent = new Intent(context, TaskPushIntentService.class);
        intent.putExtra(EXTRA_PUSH_FOR_INIT, pushForInit);
        intent.putExtra(EXTRA_IS_SWITCH_BT, isSwitch);
        intent.putExtra(EXTRA_SWITCH_BT_ACTION, action);
        context.startService(intent);
    }

    // 数据文件去重
    public static void doUniqueDataSourceFile(){
        Context context = Config.getContext();
        Intent intent = new Intent(context, UniqueDataSourceFileIntentService.class);
        context.startService(intent);
    }

    public static void doClearData(){
        Context context = Config.getContext();
        Intent intent = new Intent(context, ClearDataIntentService.class);
        context.startService(intent);
    }

    public static void doFileUpdate(JSONObject msgObject) {
        Context context = Config.getContext();
        Intent intent = new Intent(context, FileUpdateIntentService.class);
        intent.putExtra(EXTRA_UPDATE_OBJ, msgObject.toString());
        context.startService(intent);

        if (instance == null) {
            DataSourceUpdateModule.newInstance();
        }
    }

    public static void doSendHistoryBt(JSONObject msgObject) {
        Context context = Config.getContext();
        Intent intent = new Intent(context, SendHistoryBtIntentServer.class);
        intent.putExtra(EXTRA_UPDATE_OBJ, msgObject.toString());
        context.startService(intent);

        if (instance == null) {
            DataSourceUpdateModule.newInstance();
        }
    }

    public static void doDelFiles(JSONObject msgObject){
        String downloadKey = DownloadManager.getDownloadKey();
        Service service = getServiceFromJson(msgObject);
        service.setDownloadKey(downloadKey);
//        DBManager.getDaoSession().getServiceDao().insert(service);
        ServiceHelper.getInstance().insertOrRelease(service);
        long currentBtId = SPManager.getInstance().getCurrentBtId();

        JSONObject content = msgObject.getJSONObject("content");
        JSONArray ids = content.getJSONArray("ids");
        if (ids != null && ids.size() > 0) {
            Long[] keys = new Long[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                JSONObject delItem = ids.getJSONObject(i);
                Long id = Long.valueOf(delItem.getString("id"));
                if (id.equals(currentBtId)) {
                    SPManager.getInstance().saveCurrentBtId(-1L);
                }
                keys[i] = id;
            }
            DBManager.getDaoSession().getBroadcastTableDao().deleteByKeyInTx(keys);

            JSONObject responseContent = new JSONObject();
            responseContent.put("ids", ids);
            ReportMsg reportMsg = new ReportMsg(1, responseContent, downloadKey);
            EventBus.getDefault().post(reportMsg);
        } else {
            JSONObject responseContent = new JSONObject();
            responseContent.put("msg", "删除播表数组不存在或者为空");
            ReportMsg reportMsg = new ReportMsg(0, responseContent, downloadKey);
            EventBus.getDefault().post(reportMsg);
        }
    }

    public static void playActionBt(JSONObject msgObject) {
        String downloadKey = DownloadManager.getDownloadKey();
        Service service = getServiceFromJson(msgObject);
        service.setDownloadKey(downloadKey);
        ServiceHelper.getInstance().insertOrRelease(service);

        JSONObject content = msgObject.getJSONObject("content");
        String id = content.getString("id");
        if (id != null) {
            playBtById(id);

            JSONObject responseContent = new JSONObject();
            responseContent.put("id", id);
            ReportMsg reportMsg = new ReportMsg(1, responseContent, downloadKey);
            EventBus.getDefault().post(reportMsg);
        } else {
            JSONObject responseContent = new JSONObject();
            responseContent.put("msg", "缺少播放Id");
            ReportMsg reportMsg = new ReportMsg(0, responseContent, downloadKey);
            EventBus.getDefault().post(reportMsg);
        }
    }

    /**
     * 播放制定id播表
     * @param id 数据库中播表id， -1为播放当前播表
     */
    public static void playBtById(@NonNull String id) {
        if (id.equals("-1")) {
            id = SPManager.getInstance().getCurrentBtId() + "";
        }

        Context context = Config.getContext();
        Intent intent = new Intent(context, TaskPushIntentService.class);
        intent.putExtra(EXTRA_IS_PLAY_ACTION, id);
        context.startService(intent);
    }

    public static Service getServiceFromJson(JSONObject msgRoot) {
        Service service = new Service();
        service.setFromid(msgRoot.getString("fromid"));
        service.setResult(0);
        service.setMsgcw(msgRoot.getString("msgcw"));
        service.setMsgid(msgRoot.getString("msgid"));
        service.setMsgtype(msgRoot.getString("msgtype"));
        service.setToid(msgRoot.getString("toid"));
        service.setTs(System.currentTimeMillis());
        return service;
    }

    public static void createItemTypeFile(ContentItemBean itemBean) {
        if(itemBean == null) return;
        // 创建 itemtype 文件
        String itemTypeFilePath = SPManager.getInstance().getAppDataPath()
                + "/" + System.currentTimeMillis() + "." + itemBean.getItemtype();
        File file = new File(itemTypeFilePath);
        if (FileUtils.createOrExistsFile(file)) {
            JSONObject tempJSON = JSONUtils.toJSONObject(itemBean);
            tempJSON.put("itemTypeLastModified", file.lastModified());
            tempJSON.put("itemTypePath", itemTypeFilePath);
            FileIOUtils.writeFileFromString(itemTypeFilePath, tempJSON.toString(), false);

            if (!TextUtils.isEmpty(itemBean.getPath())) {
                FileMapping fileMapping = new FileMapping(null, itemBean.getPath(), itemTypeFilePath);
                DBManager.getDaoSession().getFileMappingDao().insert(fileMapping);
            }
        }
    }

    public static JSONObject getBtContent(BroadcastTable bt) {
        if (bt == null) {
            return null;
        }
        JSONObject btObject = new JSONObject();
        btObject.put("id", bt.getId());
        btObject.put("name", bt.getName());
        JSONArray itemsArray = new JSONArray();
        for (Screen screen : bt.getScreens()) {
            for (App app : screen.getApps()) {
                JSONArray items = JSON.parseArray(app.getItems());
                for (int i = 0; i < items.size(); i++) {
                    JSONArray item = items.getJSONArray(i);
                    for (int j = 0; j < item.size(); j++) {
                        JSONObject itemItem = item.getJSONObject(j);
                        String p = "";
                        if (itemItem.containsKey("name")) {
                            p = itemItem.getString("name");
                        } else if (itemItem.containsKey("url")) {
                            p = itemItem.getString("url");
                        } else if (itemItem.containsKey("content")) {
                            p = itemItem.getString("content");
                        }

                        if (!p.equals("")) {
                            itemsArray.add(p);
                        }
                    }
                }
            }
        }
        btObject.put("items", itemsArray);
        btObject.put("time", bt.getModifyDate().getTime());
        return btObject;
    }

    public static JSONObject getBtDetailContent(BroadcastTable bt) {
        JSONObject btObject = new JSONObject();
        if (bt == null) {
            return null;
        }

        btObject.put("id", bt.getBtId());
        btObject.put("name", bt.getName());
        JSONArray screens = new JSONArray();
        for (Screen screen : bt.getScreens()) {
            JSONObject screenObject = new JSONObject();
            screenObject.put("startTime", TimeUtils.date2String(screen.getStart()));
            screenObject.put("endTime", TimeUtils.date2String(screen.getEnd()));
            JSONArray itemsArray = new JSONArray();
            for (App app : screen.getApps()) {
                JSONArray items = JSON.parseArray(app.getItems());
                for (int i = 0; i < items.size(); i++) {
                    JSONArray item = items.getJSONArray(i);
                    for (int j = 0; j < item.size(); j++) {
                        JSONObject itemItem = item.getJSONObject(j);
                        String p = "";
                        if (itemItem.containsKey("name")) {
                            p = itemItem.getString("name");
                        } else if (itemItem.containsKey("url")) {
                            p = itemItem.getString("url");
                        } else if (itemItem.containsKey("content")) {
                            p = itemItem.getString("content");
                        }

                        if (!p.equals("")) {
                            itemsArray.add(p + "---" + itemItem.getString("len"));
                        }
                    }
                }
            }
            screenObject.put("items", itemsArray);
            screens.add(screenObject);
        }
        btObject.put("screens", screens);
        btObject.put("time", bt.getModifyDate().getTime());
        return btObject;
    }

    public static BroadcastTable getCurrentBt() {
        long currentBtId = SPManager.getInstance().getCurrentBtId();
        return DBManager.getDaoSession().getBroadcastTableDao().load(currentBtId);
    }

}
