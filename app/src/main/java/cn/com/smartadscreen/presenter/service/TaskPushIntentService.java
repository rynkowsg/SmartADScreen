package cn.com.smartadscreen.presenter.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.stetho.common.LogUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.main.ui.view.SmartToast;
import cn.com.smartadscreen.model.bean.InitTaskPush;
import cn.com.smartadscreen.model.bean.TaskPush;
import cn.com.smartadscreen.model.db.entity.App;
import cn.com.smartadscreen.model.db.entity.BroadcastTable;
import cn.com.smartadscreen.model.db.entity.Screen;
import cn.com.smartadscreen.model.db.gen.BroadcastTableDao;
import cn.com.smartadscreen.model.db.manager.AppHelper;
import cn.com.smartadscreen.model.db.manager.BroadcastTableHelper;
import cn.com.smartadscreen.model.db.manager.DBManager;
import cn.com.smartadscreen.model.db.manager.ScreenHelper;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;


public class TaskPushIntentService extends IntentService {

    private int startId;

    private BroadcastTableHelper helper;
    private boolean judgeNew;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TaskPushIntentService(String name) {
        super(name);
    }

    public TaskPushIntentService() {
        this("TaskPushIntentService");
    }

    // 初始化实时推送集合和延时推送集合
    private List<InitTaskPush> readyToInitPushTasks = new ArrayList<>();
    private List<InitTaskPush> readyToInitPushDelayTasks = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        helper = BroadcastTableHelper.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startId = startId;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            handle(intent);
        } catch (Exception c) {
            c.printStackTrace();
            SmartToast.error("播表分发程序发生异常");
            stopSelf(startId);
        }
    }

    private void handle(Intent intent) {
        Logger.i("执行推送任务");

        LogUtil.d("live", " TaskPushIntentService onHandleIntent");

        boolean pushForInit = intent.getBooleanExtra(DataSourceUpdateModule.EXTRA_PUSH_FOR_INIT, false);
        // 是否切换播表
        boolean isSwitchBt = intent.getBooleanExtra(DataSourceUpdateModule.EXTRA_IS_SWITCH_BT, false);
        Long currentBtId = SPManager.getInstance().getCurrentBtId();
        // 是否播放指定播表
        String playActionId = intent.getStringExtra(DataSourceUpdateModule.EXTRA_IS_PLAY_ACTION);

        // 播表集合
        List<BroadcastTable> list;

        if (pushForInit) {

            //如果app启动时间为 08:00, 为防止 开始时间为 08:00:55 非整数秒的播表不能播放，
            //将查询时间延后为当前时间的后一分钟，
            //即，查询时间设置为 08:01:00
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, 0);
            calendar.add(Calendar.MINUTE, 1);
            long queryTime = calendar.getTimeInMillis();

            list = new ArrayList<>();
            BroadcastTable needPlayTable = helper.getNearByBroadcastIdWhereIsNeedDelay(queryTime);
            if (needPlayTable != null) {
                list.add(needPlayTable);
            }
            list.addAll(helper.queryAllDelayBroadcastTable(queryTime));

            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native", "播表初始化"));

            if (!list.isEmpty()) {
                for (BroadcastTable bt : list) {
                    bt.resetScreens();
                    Logger.i("遍历BroadcastTable:" + bt.getBg());
                    List<Screen> screens = ScreenHelper.getInstance().query(bt.getId());
                    judgeNew = true;
                    for (Screen screen : screens) {
                        handleScreen(screen, true, bt);
                    }
                }
                SmartToast.info(" 消息即将推送展示! ");
            } else {
                SmartToast.info(" 没有获取到播放列表! ");
                SPManager.getInstance().saveCurrentBtId(-1L);
                // nothing to show
                JSONObject nothingJson = new JSONObject();
                nothingJson.put("datatype", "nothing EmptyPage");
                TaskPush taskPush = new TaskPush(nothingJson.toString());
                EventBus.getDefault().post(taskPush);
            }

            checkUnFinishedBroadcastTable();

            return;
        }

        if (playActionId != null) {

            LogUtil.d("live", " 播放指定播表, 指定Id: " + playActionId);

            Logger.i("播放指定播表, 指定Id: " + playActionId);
            BroadcastTable broadcastTable = BroadcastTableHelper.getInstance()
                    .queryById(Long.valueOf(playActionId));
            list = new ArrayList<>();
            if (broadcastTable != null) {
                list.add(broadcastTable);
            }
        } else if (isSwitchBt && currentBtId != -1) {

            LogUtil.d("live", "切换播表意图, 当前播表Id: " + currentBtId);

            Logger.i("切换播表意图, 当前播表Id: " + currentBtId);
            String action = intent.getStringExtra(DataSourceUpdateModule.EXTRA_SWITCH_BT_ACTION);
            list = new ArrayList<>();

            switch (action) {
                case DataSourceUpdateModule.ACTION_SWITCH_BT_LEFT:

                    BroadcastTable left = BroadcastTableHelper.getInstance()
                            .queryPreBroadcastTableById(currentBtId);

                    if (left != null)
                        list.add(left);

                    break;
                case DataSourceUpdateModule.ACTION_SWITCH_BT_RIGHT:

                    BroadcastTable next = BroadcastTableHelper.getInstance()
                            .getNextBroadcastTableById(currentBtId);

                    if (next != null)
                        list.add(next);

                    break;

            }

        } else {

            LogUtil.d("live", "query db BroadcastTableDao ");

            list = DBManager.getDaoSession().getBroadcastTableDao()
                    .queryBuilder()
                    .where(BroadcastTableDao.Properties.Finished.eq(true))
                    .list();
        }


        if (list != null && list.size() > 0) {
            List<Screen> pushScreen = new ArrayList<>();
            if (isSwitchBt || playActionId != null) {
                BroadcastTable bt = list.get(list.size() - 1);
                bt.resetScreens();

                List<Screen> screens = ScreenHelper.getInstance().query(bt.getId());
                for (Screen screen : screens) {
                    if (checkScreen(screen)) {
                        pushScreen.add(screen);
                    } else {
                        bt.delete();
                        SmartToast.error("播表过期，播表自动清理!");
                        pushScreen.clear();
                        break;
                    }
                }
                for (Screen screen : pushScreen) {
                    handleScreen(screen, false, bt);
                }
            } else {
                BroadcastTable bt = list.get(list.size() - 1);
                bt.resetScreens();

                List<Screen> screens = ScreenHelper.getInstance().query(bt.getId());
                for (Screen screen : screens) {
                    if (checkScreen(screen)) {
                        pushScreen.add(screen);
                    } else {
                        bt.delete();
                        SmartToast.error("播表过期, 播表自动清理!");
                        pushScreen.clear();
                        break;
                    }
                }
                for (Screen screen : pushScreen) {
                    handleScreen(screen, false, bt);
                }
            }

            LogUtil.d("live", " 消息即将推送展示! ");
            SmartToast.info(" 消息即将推送展示! ");
        } else {
            SmartToast.info(" 没有获取到播放列表! ");
            // nothing to show
            JSONObject nothingJson = new JSONObject();
            nothingJson.put("datatype", "nothing");
            TaskPush taskPush = new TaskPush(nothingJson.toString());
            EventBus.getDefault().post(taskPush);
        }

    }

    private boolean checkScreen(Screen screen) {
        return screen != null && screen.getEnd().getTime() > System.currentTimeMillis();
    }

//    private void  handleBg(String bg){
//        JSONObject bgJson= new JSONObject();
//        bgJson.put("datatype","bg");
//        bgJson.put("background",bg);
//
//    }

    private void handleScreen(Screen screen, boolean pushForInit, BroadcastTable parentBt) {
        // 新数据 先修改为老数据
        screen.setNewLine(false);
        screen.update();
        screen.resetApps();

        // 判断当前播表是否不需要回放
        Logger.i("判断是否需要回放");
        if (SPManager.getManager().contains(SPManager.KEY_IGNORE_BT_ID)) {
            long btId = SPManager.getManager().getLong(SPManager.KEY_IGNORE_BT_ID, -1);
            Logger.i("不需要回放的播表:" + btId);
            if (btId != -1) {
                Logger.i("删除播表!");
                SPManager.getManager().remove(SPManager.KEY_IGNORE_BT_ID);
                DBManager.getDaoSession().getBroadcastTableDao().deleteByKey(btId);
                SPManager.getManager().put(SPManager.KEY_CURRENT_BT_ID, -1L);
            }
        }

        List<App> apps = AppHelper.getInstance().queryByScreenId(screen.getId());

//        List<Application> apps = screen.getApps();
        JSONObject screenJson = new JSONObject();
        if (parentBt.getLogo() != null) {
            screenJson.put("logo", parentBt.getLogo());
        }
        if (!TextUtils.isEmpty(parentBt.getComeFrom()) && parentBt.getComeFrom().contains("-")) {
            screenJson.put("totalLength", Integer.valueOf(parentBt.getComeFrom().split("-")[1]));
        }
        screenJson.put("sid", screen.getSid());
        screenJson.put("utype", screen.getUtype());
        screenJson.put("times", screen.getTimes());
        screenJson.put("size", screen.getSize());
        screenJson.put("layout", JSON.parseObject(screen.getLayout()));
        screenJson.put("priority", screen.getPriority());
        screenJson.put("apps_relation", JSON.parseArray(screen.getAppsRelation()));
        screenJson.put("start", screen.getStart().getTime());

        JSONArray appArray = new JSONArray();
        for (App app : apps) {
            JSONObject appJson = new JSONObject();
            appJson.put("aid", app.getAid());
            appJson.put("items", JSON.parseArray(app.getItems()));
            appArray.add(appJson);
        }

        screenJson.put("apps", appArray);

        // 推送Json
        JSONObject pushJson = new JSONObject();
        pushJson.put("datatype", "add");
        pushJson.put("screen", screenJson);
        //添加background 背景图片
//        if (judgeNew == true) {
        if (parentBt.getBg() != null) {
            pushJson.put("background", JSON.parse(parentBt.getBg()));
            Logger.i("background:" + parentBt.getBg());

        }
//            judgeNew = false;
//        }

        Logger.i("推送message:" + pushJson.toString());

        // 移除Json
        JSONObject removeJson = new JSONObject();
        removeJson.put("datatype", "remove");
        removeJson.put("sid", screen.getSid());


        Date currentDate = new Date();
        if (screen.getStart().getTime() > currentDate.getTime()) {
            // 延迟推送
            // 推送

            TaskPush pushTaskPush = new TaskPush(String.valueOf(screen.getId()), pushJson.toString(), true, screen.getStart());
            pushTaskPush.setTimeType(parentBt.getTimeType());
            pushTaskPush.setLogoPath(parentBt.getLogo());
            pushTaskPush.setTableId(screen.getBtId());
            pushTaskPush.setIsAdd(true);
            // 移除 将 screenId 设为相反数，防止在执行定时任务时替换推送时要执行的定时任务
            TaskPush removeTaskPush = new TaskPush(String.valueOf(-screen.getId()), removeJson.toString(), true, screen.getEnd());

            LogUtil.d("live", "  delayPush EventBus.getDefault().post(pushTaskPush); ");
            EventBus.getDefault().post(pushTaskPush);
            EventBus.getDefault().post(removeTaskPush);
        } else if (screen.getStart().getTime() < currentDate.getTime()
                && screen.getEnd().getTime() > currentDate.getTime()) {
            // 实时推送
            // 推送
            TaskPush pushTaskPush = new TaskPush(pushJson.toString());
            pushTaskPush.setTimeType(parentBt.getTimeType());
            pushTaskPush.setLogoPath(parentBt.getLogo());
            pushTaskPush.setTableId(screen.getBtId());
            pushTaskPush.setIsAdd(true);
            // 移除
            TaskPush removeTaskPush = new TaskPush(String.valueOf(-screen.getId()), removeJson.toString(), true, screen.getEnd());
            LogUtil.d("live", "  realTimePush EventBus.getDefault().post(pushTaskPush); ");
            EventBus.getDefault().post(pushTaskPush);
            EventBus.getDefault().post(removeTaskPush);
        } else {
            // 过期
            SmartToast.wraning(screen.getSid() + " 屏幕已经过期!");
        }
    }


    /**
     * 检测未下载完成的播表，并执行
     */
    private void checkUnFinishedBroadcastTable() {

        List<BroadcastTable> downloadingBtList = BroadcastTableHelper.getInstance().queryAllUnFinished();
        Logger.i("检测未下载完成的播表，数量 " + downloadingBtList.size());
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native",
                "程序初始化检测到未下载完成播表数量 " + downloadingBtList.size()));

        foo:
        for (BroadcastTable table : downloadingBtList) {
            for (Screen screen : table.getScreens()) {
                if (!checkScreen(screen)) {
                    continue foo;
                }
            }

            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native",
                    "执行未下载完成播表，播表id=" + table.getId()));
            BroadcastTableHelper.getInstance().deleteById(table.getId());
            DataSourceUpdateModule.doDataUpdate(JSON.parseObject(table.getContent()), true);
        }
    }


}
