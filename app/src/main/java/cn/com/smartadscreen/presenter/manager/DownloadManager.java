package cn.com.smartadscreen.presenter.manager;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileUtils;
import com.facebook.stetho.common.LogUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;

import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.model.bean.DownloadErrorCode;
import cn.com.smartadscreen.model.bean.DownloadFinished;
import cn.com.smartadscreen.model.bean.DownloadProgressBean;
import cn.com.smartadscreen.model.bean.DownloadTask;
import cn.com.smartadscreen.model.bean.ReportMsg;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.db.entity.DownloadTable;
import cn.com.smartadscreen.model.db.entity.KNMapping;
import cn.com.smartadscreen.model.db.gen.BroadcastTableDao;
import cn.com.smartadscreen.model.db.gen.KNMappingDao;
import cn.com.smartadscreen.model.db.manager.DBManager;
import cn.com.smartadscreen.model.db.manager.DownloadTableHelper;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;
import cn.com.smartadscreen.utils.SmartToast;
import cn.com.smartadscreen.utils.TimerUtils;
import cn.startai.apkcommunicate.CommunicateType;
import cn.startai.apkcommunicate.StartaiCommunicate;

/**
 * Created by Taro on 2017/3/19.
 * DownloadManager 下载任务管理类
 */
public class DownloadManager {

    private static Map<String, List<DownloadTask>> downloadTaskMap = new HashMap<>();
    // url 与 uuid key 的映射表
    private static Map<String, String> urlKeyMappingMap = new HashMap<>();

    public static void addDownloadTask(String downloadKey, DownloadTask task){

        if (downloadTaskMap.containsKey(downloadKey)) {
            List<DownloadTask> downloadTasks = downloadTaskMap.get(downloadKey);
            // 如果下载任务列表中已经存在相同的任务, 不重复添加
            for (DownloadTask downloadTask : downloadTasks) {
                if (downloadTask == null || downloadTask.getUrl() == null) {
                    continue;
                }
                if(downloadTask.isSame(task)){
                    return;
                }
            }
            downloadTasks.add(task);
        } else {
            List<DownloadTask> downloadTaskList = new ArrayList<>();
            downloadTaskList.add(task);
            downloadTaskMap.put(downloadKey, downloadTaskList);
        }

        String url = task.getUrl();
        if (!urlKeyMappingMap.containsKey(url)) {
            urlKeyMappingMap.put(url, downloadKey);
        }

    }

    public synchronized static void startTask(String downloadKey, String tag){
        checkKNMapping(downloadKey, tag);

        // report to nmc
        ReportMsg reportMsg  = new ReportMsg();
        reportMsg.setResult(-1);
        reportMsg.setDownloadKey(downloadKey);
        JSONObject content = new JSONObject();
        content.put("msg", "下载开始");
        content.put("downloadKey", downloadKey);
        reportMsg.setContent(content);
        EventBus.getDefault().post( reportMsg );


        if (downloadTaskMap.containsKey(downloadKey)) {
            LogUtil.d("live", " 任务列表中存在此下载任务");
            // record remarks
            ArrayList<String> remarks = new ArrayList<>();
            StringBuilder remark ;

            List<DownloadTask> downloadTaskList = downloadTaskMap.get(downloadKey);
            for (DownloadTask downloadTask : downloadTaskList) {

                JSONObject downloadObject = new JSONObject();
                downloadObject.put("url", downloadTask.getUrl());
                downloadObject.put("localPath", downloadTask.getLocalPath());
                downloadObject.put("hash", downloadTask.getHash());
                downloadObject.put("hash2", downloadTask.getHash2() == null ? "" : downloadTask.getHash2());
                downloadObject.put("originName", downloadTask.getName());

                remark = new StringBuilder();
                remark.append("name: ");
                remark.append(downloadTask.getName());
                remark.append("\nurl: ");
                remark.append(downloadTask.getUrl());
                remark.append("\nlocalPath: ");
                remark.append(downloadTask.getLocalPath());
                remark.append("\nhash: ");
                remark.append(downloadTask.getHash());
                remark.append("\nhash2: ");
                remark.append(downloadTask.getHash2());
                remark.append("\n");
                remarks.add(remark.toString());

                LogUtil.d("live", "startTask send COMMUNICATE_TYPE_FS_DOWNLOAD ");
                StartaiCommunicate.getInstance().send(Config.getContext(),
                        CommunicateType.COMMUNICATE_TYPE_FS_DOWNLOAD,
                        downloadObject.toString());

                DownloadTableHelper.getInstance().add(DownloadTable.convert(downloadTask));

                TimerUtils.schedule(downloadKey, new CheckDownloadTask(downloadKey), 1000 * 60, 1000 * 60);
            }
            // record
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_SEND, "Native", "NMC", "发送下载消息", remarks));
        } else {

            LogUtil.d("live", " 任务列表中不存在此下载任务");

            Logger.i("任务列表中不存在此下载任务");
            KNMapping knMapping = DBManager.getDaoSession().getKNMappingDao().queryBuilder()
                    .where(KNMappingDao.Properties.DownloadKey.eq(downloadKey))
                    .unique();

            EventBus.getDefault().post(new DownloadFinished(downloadKey, knMapping.getName()));

            // report to nmc
            reportMsg  = new ReportMsg();
            reportMsg.setResult(1);
            reportMsg.setDownloadKey(downloadKey);
            content = new JSONObject();
            content.put("msg", "下载完成");
            content.put("downloadKey", downloadKey);
            reportMsg.setContent(content);
            EventBus.getDefault().post(reportMsg);
        }

    }

    public synchronized static void checkKNMapping(String downloadKey, String tag){
        KNMapping knMapping = DBManager.getDaoSession().getKNMappingDao().queryBuilder()
                .where(KNMappingDao.Properties.DownloadKey.eq(downloadKey))
                .unique();
        if (knMapping == null) {
            KNMapping mapping = new KNMapping(null, downloadKey, tag);
            DBManager.getDaoSession().getKNMappingDao().insert(mapping);
        }
    }

    public synchronized static void onProgress(DownloadProgressBean bean){
        String url = bean.getUrl();
        int progress = bean.getProgress();

        LogUtil.d("live", " onProgress: " + progress);

        //当前url 在下载集合中，即播表相关文件
        if (urlKeyMappingMap.containsKey(url)) {

            LogUtil.d("live", " urlKeyMappingMap.containsKey(url): " );

            String downloadKey = urlKeyMappingMap.get(url);
            List<DownloadTask> downloadTasks = downloadTaskMap.get(downloadKey);

            for (DownloadTask downloadTask : downloadTasks) {
                if (downloadTask.getUrl().equals(url)) {
                    downloadTask.setProgress(progress);

                    // toast download info
                    if (SPManager.getManager().getBoolean(SPManager.KEY_ENABLED_TOAST_DOWNLOAD_INFO, false)) {
                        String message = "文件名称: " + downloadTask.getName() +
                                "\n下载进度: " + progress;
                        SmartToast.normalShallowColor(message);
                    }

                    // timer to check download
                    if (progress < 100) {
                        TimerUtils.schedule(downloadKey, new CheckDownloadTask(downloadKey), 1000 * 60, 1000 * 60);
                    } else {
                        // report nmc download file success
                        // 单个文件下载完成
                        reportDownloadFileSuccess(downloadKey, downloadTask);

                        if (checkDownload(downloadKey)) {

                            LogUtil.d("live", " TimerUtils.close " );
                            TimerUtils.close(downloadKey);
                            if (downloadTaskMap.containsKey(downloadKey)) {
                                downloadTaskMap.remove(downloadKey);
                            }
                            if (urlKeyMappingMap.containsValue(downloadKey)) {
                                List<String> urls = new ArrayList<>();
                                for (Map.Entry<String, String> entry : urlKeyMappingMap.entrySet()) {
                                    if (entry.getValue().equals(downloadKey)) {
                                        urls.add(entry.getKey());
                                    }
                                }
                                for (String urlKey : urls) {
                                    urlKeyMappingMap.remove(urlKey);
                                }
                            }
                            Logger.i("下载任务执行完毕");
                            KNMapping knMapping = DBManager.getDaoSession().getKNMappingDao().queryBuilder()
                                    .where(KNMappingDao.Properties.DownloadKey.eq(downloadKey))
                                    .unique();

                            LogUtil.d("live", " post DownloadFinished : " + knMapping.getName());
                            EventBus.getDefault().post(new DownloadFinished(downloadKey, knMapping.getName()));
                            // report to nmc
                            ReportMsg reportMsg  = new ReportMsg();
                            reportMsg.setResult(1);
                            reportMsg.setDownloadKey(downloadKey);
                            JSONObject content = new JSONObject();
                            content.put("msg", "下载完成");
                            content.put("downloadKey", downloadKey);
                            reportMsg.setContent(content);
                            EventBus.getDefault().post( reportMsg );
                        }
                    }
                    break;
                }
            }
        }

    }

    private static void reportDownloadFileSuccess(String downloadKey, DownloadTask downloadTask) {
//        ReportMsg reportMsg  = new ReportMsg();
//        reportMsg.setResult(-1);
//        reportMsg.setDownloadKey(downloadKey);
//        JSONObject content = new JSONObject();
//        content.put("msg", "单个文件下载完成");
//        content.put("downloadKey", downloadKey);
//        content.put("fid", downloadTask.getUrl());
//        content.put("hash", downloadTask.getHash());
//        content.put("progress", 100);
//        reportMsg.setContent(content);
//        EventBus.getDefault().post( reportMsg );

        // 创建描述文件
        DataSourceUpdateModule.createItemTypeFile(downloadTask.getItemBean());
    }

    /**
     * 检测 downloadKey 对应的任务是否全部下载完成
     * @param downloadKey
     * @return
     */
    private static boolean checkDownload(String downloadKey) {
        List<DownloadTask> downloadTasks = downloadTaskMap.get(downloadKey);
        for (DownloadTask downloadTask : downloadTasks) {
            if (downloadTask.getProgress() < 100) {
                return false ;
            }
        }
        return true;
    }

    public synchronized static void onError(DownloadProgressBean bean){
        String url = bean.getUrl();
        String errorCode = bean.getErrorCode();

        if (urlKeyMappingMap.containsKey(url)) {
            String downloadKey = urlKeyMappingMap.get(url);
            List<DownloadTask> downloadTasks = downloadTaskMap.get(downloadKey);
            for (DownloadTask downloadTask : downloadTasks) {
                if (downloadTask.getUrl().equals(url)) {
                    downloadTask.setErrorCode(errorCode);

                    ReportMsg reportMsg  = new ReportMsg();
                    switch (errorCode) {
                        case "-1018" :
                        case "-1009":
                        case "-1005":
                            reportMsg.setResult(-1);
                            break;
                        default:
                            reportMsg.setResult(0);
                            break;
                    }

                    reportMsg.setDownloadKey(downloadKey);
                    JSONObject content = new JSONObject();
                    content.put("msg", "下载文件失败，" + DownloadErrorCode.getDownloadMsgByCode(Integer.parseInt(errorCode)));
                    content.put("errorCode", errorCode);
                    content.put("downloadKey", downloadKey);
                    content.put("fid", downloadTask.getUrl());
                    content.put("hash", downloadTask.getHash());
                    reportMsg.setContent(content);
                    EventBus.getDefault().post(reportMsg);



                    SmartToast.error(downloadTask.getName() + " 文件下载失败!");

                    //-1016 文件与hash值不匹配， -1010 URL格式不正确, -1015本地路径不合法， -1020 局域网URL与
                    if ("-1016".equals(errorCode) || "-1010".equals(errorCode) || "-1020".equals(errorCode)
                            || "-1015".equals(errorCode)) {
                        //停止请求 NMC 下载
                        TimerUtils.close(downloadKey);
                    }
                    break;
                }
            }
        }
    }

    public static String getDownloadKey(){
        return UUID.randomUUID().toString();
    }

    private static class CheckDownloadTask extends TimerTask{

        private String downloadKey ;

        private CheckDownloadTask(String downloadKey){
            this.downloadKey = downloadKey ;
        }

        @Override
        public void run() {
            // record remarks
            //TODO 增加局域网IP URL 判断
            String localIP = SPManager.getInstance().getLocalIP();//本地ip
            ArrayList<String> remarks = new ArrayList<>();
            StringBuilder remark;

            boolean isFound = false;
            List<DownloadTask> downloadTasks = downloadTaskMap.get(downloadKey);
            for (DownloadTask downloadTask : downloadTasks) {
                if (downloadTask.getProgress() < 100 &&
                        !checkLocalFile(downloadTask.getLocalPath(), downloadTask.getHash(), downloadTask.getHash2())) {

                    Logger.d("检测到未完成任务：" + downloadTask.getName());

                    isFound = true;
                    String url = downloadTask.getUrl();
                    // 下载任务没有完成
                    JSONObject downloadObject = new JSONObject();

                    downloadObject.put("url", downloadTask.getUrl());
                    downloadObject.put("localPath", downloadTask.getLocalPath());
                    downloadObject.put("hash", downloadTask.getHash());
                    downloadObject.put("hash2", downloadTask.getHash2() == null ? "" : downloadTask.getHash2());
                    downloadObject.put("originName", downloadTask.getName());

                    remark = new StringBuilder();

                    if(!TextUtils.isEmpty(localIP)){
                        try {
                            URL downloadUrl = new URL(url);
                            String downloadIP = downloadUrl.getHost();
                            if(downloadIP.startsWith("192.168") && !isSameLan(localIP, downloadIP)){
                                remark.append("本机IP地址改变，与下载地址不在同一个局域网内，取消定时检测下载任务，并删除相应播表");
                                remarks.add(remark.toString());
                                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native",
                                        "Native定时检测未完成任务, DownloadKey:" + downloadKey, remarks));
                                this.cancel();
                                DBManager.getDaoSession().getBroadcastTableDao()
                                        .queryBuilder()
                                        .where(BroadcastTableDao.Properties.DownloadKey.eq(downloadKey))
                                        .buildDelete()
                                        .executeDeleteWithoutDetachingEntities();
                                break;
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    }

                    remark.append("name: ")
                            .append(downloadTask.getName())
                            .append("\nurl: ")
                            .append(url)
                            .append("\nlocalPath: ")
                            .append(downloadTask.getLocalPath())
                            .append("\nhash: ")
                            .append(downloadTask.getHash())
                            .append("\n");
                    remarks.add(remark.toString());

                    LogUtil.d("live", "CheckDownloadTask send COMMUNICATE_TYPE_FS_DOWNLOAD ");
                    StartaiCommunicate.getInstance().send(Config.getContext(),
                            CommunicateType.COMMUNICATE_TYPE_FS_DOWNLOAD,
                            downloadObject.toString());
                }
            }

            if(!isFound) {
                TimerUtils.close(downloadKey);
                Logger.d("==================任务关闭：" + downloadKey  );
            }

            // record
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_SEND, "Native", "NMC",
                    "Native定时检测未完成任务, DownloadKey:" + downloadKey, remarks));
        }

        //判断是否是同一局域网
        private Boolean isSameLan(String ip1, String ip2){
            try{
                String[] ip1Nums = ip1.split(".");
                String[] ip2Nums = ip2.split(".");
                return ip1.startsWith("192.168") && ip2.startsWith("192.168") &&
                        Integer.parseInt(ip1Nums[2]) == Integer.parseInt(ip2Nums[2]);
            } catch (Exception e){
                return false;
            }
        }

    }

    private static boolean checkLocalFile(String filePath, String hash, String hash2) {
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

}
