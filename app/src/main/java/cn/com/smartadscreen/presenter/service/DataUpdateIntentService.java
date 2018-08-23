package cn.com.smartadscreen.presenter.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.facebook.stetho.common.LogUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.main.ui.view.SmartToast;
import cn.com.smartadscreen.model.bean.ContentItemBean;
import cn.com.smartadscreen.model.bean.DownloadTask;
import cn.com.smartadscreen.model.bean.ReportErrorMsg;
import cn.com.smartadscreen.model.bean.ReportMsg;
import cn.com.smartadscreen.model.db.entity.App;
import cn.com.smartadscreen.model.db.entity.BroadcastTable;
import cn.com.smartadscreen.model.db.entity.Screen;
import cn.com.smartadscreen.model.db.entity.Service;
import cn.com.smartadscreen.model.db.gen.BroadcastTableDao;
import cn.com.smartadscreen.model.db.manager.BroadcastTableHelper;
import cn.com.smartadscreen.model.db.manager.DBManager;
import cn.com.smartadscreen.model.db.manager.ServiceHelper;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.manager.DownloadManager;
import cn.com.smartadscreen.presenter.nmc.NmcReport;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;
import cn.com.smartadscreen.utils.AppFileUtils;
import cn.com.smartadscreen.utils.JSONUtils;


public class DataUpdateIntentService extends IntentService {

    // 每个执行逻辑仅拥有一个downloadKey
    private String downloadKey;
    // record remarks
    private ArrayList<String> checkLocalFileRemarks;
    // data folder
    private String appDataFolder;
    private int startId;

    BroadcastTableHelper mBroadcastTableHelper;
    //2007-09-06 15:55:00
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DataUpdateIntentService(String name) {
        super(name);
    }

    public DataUpdateIntentService() {
        this("DataUpdateIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBroadcastTableHelper = BroadcastTableHelper.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startId = startId;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String updateObj = intent.getStringExtra(DataSourceUpdateModule.EXTRA_UPDATE_OBJ);
        JSONObject msgRoot = JSON.parseObject(updateObj);
        LogUtil.i("msgRoot:"+msgRoot.toJSONString());
        try {
            handle(msgRoot);
        } catch (Exception e) {
            e.printStackTrace();

            // 回复云端，播表解析错误

            ReportErrorMsg errorMsg = new ReportErrorMsg();
            errorMsg.setResult(0);
            errorMsg.setMsgcw(NmcReport.getReportMsgcw(msgRoot.getString("msgcw")));
            errorMsg.setToid(msgRoot.getString("fromid"));
            errorMsg.setTs(System.currentTimeMillis());
            errorMsg.setMsgtype(msgRoot.getString("msgtype"));
            JSONObject content = new JSONObject();
            content.put("msg", "播表解析错误");
            content.put("msgInfo", e.toString());
            errorMsg.setContent(content);
            EventBus.getDefault().post(errorMsg);

            SmartToast.error("播表处理程序发生异常");
            stopSelf(startId);
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.d("live", " DataUpdateIntentService onDestroy  ");
        super.onDestroy();
    }

    private void handle(JSONObject msgRoot) {
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native", "进入 DataSourceUpdate 处理程序"));

        LogUtil.d("live", " 进入 DataSourceUpdate 处理程序  ");

        appDataFolder = AppFileUtils.getAppDataFolderPath();

        checkLocalFileRemarks = new ArrayList<>();
        msgRoot.put("ts", 0);

        JSONObject contentRoot = msgRoot.getJSONObject("content");
        Logger.i("acg原始数据:" + contentRoot.toJSONString());
        Service service = DataSourceUpdateModule.getServiceFromJson(msgRoot);

        String id = contentRoot.getString("id");
        String name = contentRoot.getString("name");
        String resolution = contentRoot.getString("resolution");
        String appType = contentRoot.getString("apptype");
        String logo = contentRoot.getString("logo");
        String from = contentRoot.toString().contains("isWechat") ? "wechat" : "startai";
        //todo new
        String bg = contentRoot.toJSONString().contains("Bg") ? contentRoot.getString("Bg") : null;
        JSONObject bgJson = contentRoot.getJSONObject("Bg");
        Logger.i("name:" + name);
        Logger.i("bg:"+bg);

        // playType: 0 -> 置顶 1 -> 不回放
        Integer playType = contentRoot.getInteger("playType");
        // totalLength 播表总时间
        Integer totalLength = contentRoot.getInteger("totalLength");
        String timeType = contentRoot.getString("timetype");

        // new
        BroadcastTable bt = new BroadcastTable();
        bt.setBtId(id);
        bt.setName(name);
        bt.setResolution(resolution);
        bt.setAppType(appType);
        bt.setContent(msgRoot.toString());
        bt.setLogo(logo);
        bt.setBg(bg);
        if (totalLength != null) {
            bt.setComeFrom(from + '-' + totalLength);
        } else {
            bt.setComeFrom(from);
        }
        bt.setCreateDate(new Date());
        bt.setModifyDate(new Date());
        bt.setTimeType(timeType);

        // 查询最后一条数据是否是该id
        List<BroadcastTable> list = DBManager.getDaoSession().getBroadcastTableDao()
                .queryBuilder()
                .orderDesc(BroadcastTableDao.Properties.Id)
                .list();
        //log
        Logger.i("播表ID：" + bt.getBtId());

        if (list.size() > 0 && list.get(0).getBtId().equalsIgnoreCase(bt.getBtId())) {
            // old
            BroadcastTable existBt = list.get(0);
            ArrayList<String> cous = new ArrayList<>();
            //log
            Logger.i("本地数据库的对应json:" + existBt.getContent());
            Logger.i("Nmc传过来的json：" + bt.getContent());
            //日志
            cous.add("本地数据库的对应json:" + existBt.getContent());
            cous.add("Nmc传过来的json：" + bt.getContent());
            cous.add("云端传入的content:" + bt.getContent());
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_SEND, "Html",
                    "Native", "播表Json数据比对", cous));

            Logger.i("云端传入的content:" + bt.getContent());

            if (existBt.getContent().equalsIgnoreCase(bt.getContent())) {
                ArrayList<String> remarks = new ArrayList<>();
                remarks.add("播表Id:" + bt.getBtId());
                remarks.add("播表名称:" + bt.getName());
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native",
                        "播表检测, 播表重复, 不做处理", remarks));
                SmartToast.wraning("播表检测, 播表重复, 不做处理");
                //log
                Logger.i("播表检测, 播表重复, 不做处理");
                Logger.i("本地数据库的对应json:" + existBt.getContent());
                Logger.i("Nmc传过来的json：" + bt.getContent());

                //播表重复，通知云端当前播表完成，或者正在下载
                ReportMsg reportMsg = new ReportMsg();
                JSONObject content = new JSONObject();
                int i = 2;

                if (existBt.getFinished()) {
                    reportMsg.setResult(1);
                    content.put("msg", "播表重复，文件下载完成");
                } else {
                    reportMsg.setResult(-1);
                    content.put("msg", "播表重复，文件正在下载");
                }
                reportMsg.setDownloadKey(existBt.getDownloadKey());
                content.put("downloadKey", existBt.getDownloadKey());
                reportMsg.setContent(content);
                EventBus.getDefault().post(reportMsg);
            } else {
                // 播表不重复
                JSONArray screens = contentRoot.getJSONArray("screens");
                List<JSONObject> screenList = Arrays.asList(screens.toArray(new JSONObject[screens.size()]));

                Collections.sort(screenList, (o1, o2) -> {
                    long startTime1 = TimeUtils.string2Millis(o1.getString("start"));
                    long startTime2 = TimeUtils.string2Millis(o2.getString("start"));
                    return startTime1 < startTime2 ? 0 : 1;
                });

                if (TimeUtils.string2Millis(screenList.get(0).getString("start")) > System.currentTimeMillis()) {
                    bt.setNeedDelay(true);
                } else {
                    bt.setNeedDelay(false);
                }

                existBt.resetFiled(bt);
                existBt.update();
                saveStickBt(playType, existBt.getId());
                downloadKey = existBt.getDownloadKey();
                service.setDownloadKey(downloadKey);
                ServiceHelper.getInstance().insertOrRelease(service);

                // 比较背景图片是否一致
                String exBg = existBt.getBg();
                if (exBg != null && bg != null && !exBg.equals(bg)) {

                    handleBg(bgJson,existBt);
                }

                handleScreens(existBt, screens);

                Logger.i("解析完毕,本地文件检查结果\n" + checkLocalFileRemarks.toString());
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native", "本地文件检查", checkLocalFileRemarks));

                Logger.i("发送下载任务");
                DownloadManager.startTask(downloadKey, DataSourceUpdateModule.TAG);
            }

        } else {

            // insert
            bt.setFinished(false);
            downloadKey = UUID.randomUUID().toString();
            service.setDownloadKey(downloadKey);
            ServiceHelper.getInstance().insertOrRelease(service);
            bt.setDownloadKey(downloadKey);


            Logger.i("DownloadKey 生成完毕, DownloadKey: " + downloadKey + " ,解析开始!");
            JSONArray screens = contentRoot.getJSONArray("screens");


            List<JSONObject> screenList = Arrays.asList(screens.toArray(new JSONObject[screens.size()]));

            Collections.sort(screenList, (o1, o2) -> {
                long startTime1 = TimeUtils.string2Millis(o1.getString("start"));
                long startTime2 = TimeUtils.string2Millis(o2.getString("start"));
                return startTime1 < startTime2 ? 0 : 1;
            });

            if (TimeUtils.string2Millis(screenList.get(0).getString("start")) > System.currentTimeMillis()) {
                bt.setNeedDelay(true);
            } else {
                bt.setNeedDelay(false);
            }

            mBroadcastTableHelper.insert(bt);
            saveStickBt(playType, bt.getId());
            //todo 比较背景图片是否一致
            if (bg != null) {
                handleBg(bgJson,bt);
            }
            handleScreens(bt, screens);

            Logger.i("解析完毕,本地文件检查结果\n" + checkLocalFileRemarks.toString());
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native", "本地文件检查", checkLocalFileRemarks));

            Logger.i("发送下载任务:");
            DownloadManager.startTask(downloadKey, DataSourceUpdateModule.TAG);

        }


        ArrayList<String> remarks = new ArrayList<>();
        remarks.add("传入的json数据" + msgRoot.toString());
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native",
                "Native", "准备数据文件去重", remarks));

        // 数据文件去重
        Logger.i("准备数据文件去重!");
        DataSourceUpdateModule.doUniqueDataSourceFile();

        LogUtil.d("live", " 退出 DataSourceUpdate 处理程序  ");

    }

    private void saveStickBt(Integer playType, Long btId) {
        if (playType != null) {
            switch (playType) {
                case 0:
                    SPManager.getManager().put(SPManager.KEY_STICK_TOP_BT_ID, btId);
                    break;
                case 1:
                    SPManager.getManager().put(SPManager.KEY_IGNORE_BT_ID, btId);
                    break;
                case 2:
                    // short message
                    break;
            }
        }
    }


    /**
     * 处理下载背景图片
     **/
    private void handleBg(JSONObject bgJsonString,BroadcastTable parentBt) {


        ContentItemBean contentBgBean = JSON.parseObject(bgJsonString.toJSONString(), ContentItemBean.class);
        Logger.i("bgJson", contentBgBean.toString());

        if (contentBgBean.getFile() != null && contentBgBean.getHash() != null) {
            String file = contentBgBean.getFile();
            String hash = contentBgBean.getHash();
            String hash2 = contentBgBean.getHash2();
            String name = contentBgBean.getName();
            name = (name == null ? hash : name);

            if (TextUtils.isEmpty(file)) {
                SmartToast.error("播表格式错误, BG字段中缺少 file 字段!");
                checkLocalFileRemarks.add("Bg字段中缺少 file 字段,不检查");
                stopSelf();
                return;
            }

            String fileName;
            if (TextUtils.isEmpty(hash) && !TextUtils.isEmpty(file)) {
                String urlHash = EncryptUtils.encryptMD5ToString(file).toLowerCase();
                fileName = hash2 + "-" + urlHash + getExtNameByName("imageplayer", name);
            } else {
                fileName = hash + getExtNameByName("imageplayer", name);
            }
            String filePath = appDataFolder + fileName;

            // record remark
            StringBuilder remark = new StringBuilder();
            remark.append("文件名称: ");
            remark.append(name);
            remark.append("\n本地文件路径: ");
            remark.append(filePath);
            contentBgBean.setPath(filePath);
            bgJsonString.put("path",filePath);

            parentBt.setBg(bgJsonString.toJSONString());
            parentBt.update();



            if (!checkLocalFile(filePath, hash, hash2)) {
                remark.append("\n检查结果: 本地背景图片不存在\n");

                DownloadTask downloadTask = null;
                if (hash.equals("") || hash.length() == 0) {
                    downloadTask = new DownloadTask(file, filePath, name, hash, hash2);
                    downloadTask.setItemBean(contentBgBean);
                } else {
                    downloadTask = new DownloadTask(file, filePath, name, hash);
                    downloadTask.setItemBean(contentBgBean);
                }
                Logger.i("添加下载路径:" + filePath);
                DownloadManager.addDownloadTask(downloadKey, downloadTask);
            } else {
                remark.append("\n检查结果: 本地存在\n");
            }

            checkLocalFileRemarks.add(remark.toString());


        } else {
            if (contentBgBean.getPath() == null) {
                DataSourceUpdateModule.createItemTypeFile(contentBgBean);
            }
        }

    }

    private void handleScreens(BroadcastTable parentBt, JSONArray screens) {

        // 下载logo
        if (!TextUtils.isEmpty(parentBt.getLogo())) {
//            String logoPath = appDataFolder +
//                    "logo_bt_" + parentBt.getId() + parentBt.getLogo().substring(parentBt.getLogo().lastIndexOf("."));
//            DownloadManager.addDownloadTask(downloadKey, new DownloadTask(parentBt.getLogo(), logoPath, logoPath, ""));
            parentBt.setLogo(parentBt.getLogo());
            parentBt.update();
        }

        List<Screen> parentBtScreens = parentBt.getScreens();
        if (parentBtScreens.size() > 0) {

            ArrayList<String> screenUpdateRemarks = new ArrayList<>();
            int parentScreenSize = parentBtScreens.size();
            for (int i = 0; i < parentScreenSize; i++) {
                if (parentBtScreens.get(i).getContent().equals(screens.getJSONObject(i).toString())) {
                    // Screen未更新
                    screenUpdateRemarks.add(parentBtScreens.get(i).getSid() + ": 未更新");
                } else {
                    handleScreen(parentBt, screens.getJSONObject(i), parentBtScreens.get(i));
                    screenUpdateRemarks.add(parentBtScreens.get(i).getSid() + ": 已更新");
                }
            }

            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native",
                    "局部Screen更新记录", screenUpdateRemarks));

        } else {

            int screenSize = screens.size();
            for (int i = 0; i < screenSize; i++) {
                JSONObject screenObj = screens.getJSONObject(i);
                handleScreen(parentBt, screenObj, null);
            }

        }

    }

    private void handleScreen(BroadcastTable parentBt, JSONObject screenObj, Screen existScreen) {
        Screen screen = new Screen();

        String sid = screenObj.getString("sid");
        String times = screenObj.getString("times");
        String start = screenObj.getString("start");
        String end = screenObj.getString("end");
        String priority = screenObj.getString("priority");
        String utype = screenObj.getString("utype");
        String size = screenObj.getString("size");
        JSONObject layout = screenObj.getJSONObject("layout");
        JSONArray appsRelation = screenObj.getJSONArray("apps_relation");

        screen.setSid(sid);
        screen.setTimes(times);
        screen.setStart(TimeUtils.string2Date(start));
        screen.setEnd(TimeUtils.string2Date(end));
        screen.setPriority(priority);
        screen.setUtype(utype);
        screen.setSize(size);
        screen.setLayout(layout.toString());
        screen.setAppsRelation(appsRelation.toString());
        screen.setContent(screenObj.toString());
        screen.setNewLine(true);

        screen.setBtId(parentBt.getId());

        if (existScreen != null) {
            // 已经存在Screen且需要更新的情况下执行
            existScreen.resetFiled(screen);
            existScreen.update();
            for (App app : existScreen.getApps()) {
                DBManager.getDaoSession().getAppDao().delete(app);
            }
            screen = existScreen;
        } else {
            DBManager.getDaoSession().getScreenDao().insert(screen);
        }

        if (screen.getEnd().getTime() > System.currentTimeMillis()) {
            JSONArray apps = screenObj.getJSONArray("apps");
            handleApps(screen, apps);
        } else {
            // screen 过期
            ArrayList<String> screenEndTime = new ArrayList<>();
            screenEndTime.add("Screen Id: " + screen.getSid());
            screenEndTime.add("Screen结束时间: " + TimeUtils.date2String(screen.getEnd()));
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native"
                    , "Screen检测, Screen过期", screenEndTime));

        }
    }

    private void handleApps(Screen parentScreen, JSONArray apps) {

        int appSize = apps.size();
        App[] appArray = new App[appSize];
        for (int i = 0; i < appSize; i++) {

            JSONObject appObj = apps.getJSONObject(i);
            App app = new App();

            String aid = appObj.getString("aid");
            JSONArray items = appObj.getJSONArray("items");

            // 解析item中的下载信息
            handleItem(items);

            app.setAid(aid);
            app.setItems(items.toString());
            app.setScreenId(parentScreen.getId());
            appArray[i] = app;

        }

        DBManager.getDaoSession().getAppDao().insertInTx(appArray);

    }

    private void handleItem(JSONArray items) {

        int itemsSize = items.size();
        for (int i = 0; i < itemsSize; i++) {
            JSONArray item = items.getJSONArray(i);
            int itemSize = item.size();
            for (int j = 0; j < itemSize; j++) {
                // get item
                JSONObject itemObj = item.getJSONObject(j);
                handleDownload(itemObj);
            }

        }

    }

    private void handleDownload(JSONObject itemObj) {
        // item 中含有 file 字段和 hash 字段代表存在下载任务
        // file 即 URL
        // 有可能是微信端发送的内容
//        if (itemObj.containsKey("file") && itemObj.containsKey("hash")
//                || itemObj.containsKey("isWechat")) {
        ContentItemBean itemBean =
                JSONUtils.parseObject(itemObj.toJSONString(),
                        ContentItemBean.class);

        Logger.json(itemBean.toString());
        if ((itemBean.getFile() != null && itemBean.getHash() != null)
                || itemBean.getIsWechat() == 1) {
            // item 含有下载信息
            String file = itemBean.getFile();
            String hash = itemBean.getHash();
            String hash2 = itemBean.getHash2();
            String name = itemBean.getName();
            name = (name == null ? hash : name);

            if (TextUtils.isEmpty(file)) {
                SmartToast.error("播表格式错误, 缺少 file 字段!");
                checkLocalFileRemarks.add("缺少 file 字段,不检查");
                stopSelf();
                return;
            }

            String fileName;
            if (TextUtils.isEmpty(hash) && !TextUtils.isEmpty(file)) {
                String urlHash = EncryptUtils.encryptMD5ToString(file).toLowerCase();
                fileName = hash2 + "-" + urlHash + getExtNameByName(itemBean.getItemtype(), name);
            } else {
                fileName = hash + getExtNameByName(itemBean.getItemtype(), name);
            }
            String filePath = appDataFolder + fileName;

            // record remark
            StringBuilder remark = new StringBuilder();
            remark.append("文件名称: ");
            remark.append(name);
            remark.append("\n本地文件路径: ");
            remark.append(filePath);

            itemBean.setPath(filePath);
            itemObj.put("path", filePath);

            if (!checkLocalFile(filePath, hash, hash2)) {
                remark.append("\n检查结果: 本地不存在\n");

                DownloadTask downloadTask = null;
                if (hash.equals("") || hash.length() == 0) {
                    downloadTask = new DownloadTask(file, filePath, name, hash, hash2);
                    downloadTask.setItemBean(itemBean);
                } else {
                    downloadTask = new DownloadTask(file, filePath, name, hash);
                    downloadTask.setItemBean(itemBean);
                }
                Logger.i("添加下载路径:" + filePath);
                DownloadManager.addDownloadTask(downloadKey, downloadTask);
            } else {
                remark.append("\n检查结果: 本地存在\n");
            }

            checkLocalFileRemarks.add(remark.toString());

        } else {
            if (itemBean.getPath() == null) {
                DataSourceUpdateModule.createItemTypeFile(itemBean);
            }
        }

    }

    private boolean checkLocalFile(String filePath, String hash, String hash2) {
        File file = new File(filePath);
        if (file.exists()) {
            if (TextUtils.isEmpty(hash)) {
                String name = file.getName().substring(0, file.getName().lastIndexOf("."));
                String[] subNames = name.split("-");
                String urlHash = subNames[1];
                Logger.i("urlHash:" + urlHash);
                long size = file.length();
                String localFileHash2 = EncryptUtils.encryptMD5ToString(urlHash + size);
                Logger.i("检测本地文件Hash2:" + localFileHash2 + ", hash2:" + hash2);
                if (localFileHash2.equalsIgnoreCase(hash2)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                String localFileHash = FileUtils.getFileMD5ToString(file);
                Logger.i("检测本地文件Hash:" + localFileHash + ", hash:" + hash);
                if (hash.equalsIgnoreCase(localFileHash)) {
                    return true;
                } else {
                    boolean result = file.delete();
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    /**
     * 获取文件后缀名
     *
     * @param itemType item 类型
     * @param name     文件名
     * @return 后缀名
     */
    private String getExtNameByName(String itemType, String name) {
        if (TextUtils.isEmpty(name))
            return "";
        String extName = FileUtils.getFileExtension(name);
        //默认后缀名
        if (TextUtils.isEmpty(extName)) {
            switch (itemType) {
                case "mediaplayer":
                    extName = "mp4";
                    break;
                case "imageplayer":
                    extName = "jpg";
                    break;
                case "musicplayer":
                    extName = "mp3";
                    break;
            }
        } else {
            extName = "." + extName;
        }
        return extName;
    }

}
