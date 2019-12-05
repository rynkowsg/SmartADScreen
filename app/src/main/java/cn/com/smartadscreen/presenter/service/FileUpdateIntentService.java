package cn.com.smartadscreen.presenter.service;

import android.app.IntentService;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.smartadscreen.model.bean.DownloadTask;
import cn.com.smartadscreen.model.db.entity.Service;
import cn.com.smartadscreen.model.db.manager.ServiceHelper;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.manager.DownloadManager;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;


public class FileUpdateIntentService extends IntentService {

    public FileUpdateIntentService() {
        super("FileUpdateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /**
         * [
         *     {
         *        "file":"http://192.168.50.1/fid ",
         *        "hash":"125125ssq12a54q11a25qa125aq",
         *        "name":"市场营销(BTEC)"
         *     }
         *
         * ]
         */
        String downloadKey = DownloadManager.getDownloadKey();
        JSONObject msgObj = JSON.parseObject(intent.getStringExtra(DataSourceUpdateModule.EXTRA_UPDATE_OBJ));
        Service service = getServiceFromJson(msgObj);
        service.setDownloadKey(downloadKey);
//        DBManager.getDaoSession().getServiceDao().insert(service);
        ServiceHelper.getInstance().insertOrRelease(service);
        JSONObject contentObj = msgObj.getJSONObject("content");
        JSONArray files = contentObj.getJSONArray("files");
        for (int i = 0, len = files.size(); i < len; i++) {
            JSONObject contentItem = files.getJSONObject(i);
            String url = contentItem.getString("file");
            String hash = contentItem.getString("hash");
            String name = contentItem.getString("name");
            String localPath = SPManager.getInstance().getAppDataPath()
                    + "/" + name + url.substring(url.lastIndexOf("."));

            DownloadTask task = new DownloadTask(url, localPath, name, hash);
            DownloadManager.addDownloadTask(downloadKey, task);
        }

        DownloadManager.startTask(downloadKey, DataSourceUpdateModule.TAG);
    }


    private Service getServiceFromJson(JSONObject msgRoot) {
        Service service = new Service();
        service.setMethod(msgRoot.getString("method"));
        service.setRequestId(msgRoot.getString("requestId"));
        return service;
    }
}
