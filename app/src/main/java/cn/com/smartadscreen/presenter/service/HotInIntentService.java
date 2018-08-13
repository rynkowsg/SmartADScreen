package cn.com.smartadscreen.presenter.service;


import android.app.IntentService;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.FileUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import cn.com.smartadscreen.model.bean.DownloadTask;
import cn.com.smartadscreen.model.bean.ReportMsg;
import cn.com.smartadscreen.model.db.entity.Service;
import cn.com.smartadscreen.model.db.manager.ServiceHelper;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.manager.DownloadManager;
import cn.com.smartadscreen.presenter.update.HotCodeUpdateModule;


public class HotInIntentService extends IntentService {

    private Service service;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public HotInIntentService(String name) {
        super(name);
    }

    public HotInIntentService(){
        this("HotInIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // handler intent
        String updateType =
                intent.getStringExtra(HotCodeUpdateModule.EXTRA_UPDATE_TYPE);
        JSONObject msgObject =
                JSON.parseObject(intent.getStringExtra(HotCodeUpdateModule.EXTRA_UPDATE_OBJ));

        service = JSON.parseObject(msgObject.toString(), Service.class);

        if (HotCodeUpdateModule.UPDATE_TYPE_OS.equals(updateType)) {
            handlerWebUpdate(msgObject.getJSONObject("content"));
        }

        if (HotCodeUpdateModule.UPDATE_TYPE_FILE.equals(updateType)) {
            handlerWebFileUpdate(msgObject.getJSONObject("content"));
        }

    }

    private void handlerWebUpdate(JSONObject content){
        String downloadKey = DownloadManager.getDownloadKey();
        service.setDownloadKey(downloadKey);
        ServiceHelper.getInstance().insertOrRelease(service);

        if (content.containsKey("url") && !content.getString("url").contains("null/null") && content.containsKey("hash")) {
            String url = content.getString("url");
            String hash = content.getString("hash");
            String localPath = SPManager.getManager().getString(SPManager.KEY_APP_WEB_FOLDER)
                    + "/www.zip";
            String name = "HotCodeIn 文件更新";
            DownloadTask downloadTask = new DownloadTask(url, localPath, name, hash);

            DownloadManager.addDownloadTask(downloadKey, downloadTask);
            DownloadManager.startTask(downloadKey, HotCodeUpdateModule.TAG);
        } else {
            reportJsonException(downloadKey);
        }
    }

    private void handlerWebFileUpdate(JSONObject content){
        String downloadKey = DownloadManager.getDownloadKey();
        service.setDownloadKey(downloadKey);
        ServiceHelper.getInstance().insertOrRelease(service);

        if (content.containsKey("update")) {
            JSONArray update = content.getJSONArray("update");
            int updateSize = update.size();

            for (int i = 0; i < updateSize; i++) {
                JSONObject updateObject = update.getJSONObject(i);
                String hash = updateObject.getString("hash");
                String path = updateObject.getString("path");
                String url = updateObject.getString("url");
                String file = updateObject.getString("file");
                if (hash != null && path != null && url != null && file != null) {
                    if (checkFile(path, hash)) {
                        String localFilePath = SPManager.getManager().getString(SPManager.KEY_APP_WWW_FOLDER)
                                + "/" + path;
                        DownloadTask downloadTask = new DownloadTask(url, localFilePath, file, hash);
                        DownloadManager.addDownloadTask(downloadKey, downloadTask);
                    } else {
                        reportHashException(downloadKey, url, path, hash);
                    }
                } else {
                    reportJsonException(downloadKey);
                }
            }
            DownloadManager.startTask(downloadKey, HotCodeUpdateModule.TAG);
        } else {
            reportJsonException(downloadKey);
        }
    }

    private void reportHashException(String downloadKey, String url, String path, String hash) {
        ReportMsg reportMsg  = new ReportMsg();
        reportMsg.setResult(0);
        reportMsg.setDownloadKey(downloadKey);
        JSONObject content = new JSONObject();
        content.put("msg", "Hash 值重复, 无需下载!");
        content.put("downloadKey", downloadKey);
        content.put("url", url);
        content.put("path", path);
        content.put("hash", hash);
        reportMsg.setContent(content);
        EventBus.getDefault().post( reportMsg );
    }

    private void reportJsonException(String downloadKey) {
        ReportMsg reportMsg  = new ReportMsg();
        reportMsg.setResult(0);
        reportMsg.setDownloadKey(downloadKey);
        JSONObject content = new JSONObject();
        content.put("msg", "Json格式错误, 请检查(HotCodeIn)!");
        content.put("downloadKey", downloadKey);
        reportMsg.setContent(content);
        EventBus.getDefault().post( reportMsg );
    }

    private boolean checkFile(String path, String hash) {
        String localFilePath = SPManager.getManager().getString(SPManager.KEY_APP_WWW_FOLDER)
                + "/" + path;
        Logger.i(localFilePath + "   " + hash + "   " + FileUtils.getFileMD5ToString(localFilePath).toLowerCase() + "  " + FileUtils.isFileExists(localFilePath) );
        if (FileUtils.isFileExists(localFilePath)) {
            if (FileUtils.getFileMD5ToString(localFilePath).equalsIgnoreCase(hash)) {
                return false ;
            } else {
                return true ;
            }
        } else {
            return true ;
        }
    }

}
