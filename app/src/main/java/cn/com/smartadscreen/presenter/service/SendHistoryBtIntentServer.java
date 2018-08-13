package cn.com.smartadscreen.presenter.service;

import android.app.IntentService;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.com.smartadscreen.model.bean.ReportMsg;
import cn.com.smartadscreen.model.db.entity.BroadcastTable;
import cn.com.smartadscreen.model.db.entity.Service;
import cn.com.smartadscreen.model.db.gen.BroadcastTableDao;
import cn.com.smartadscreen.model.db.manager.DBManager;
import cn.com.smartadscreen.model.db.manager.ServiceHelper;
import cn.com.smartadscreen.presenter.manager.DownloadManager;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;


public class SendHistoryBtIntentServer extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private SendHistoryBtIntentServer(String name) {
        super(name);
    }

    public SendHistoryBtIntentServer() {
        super("SendHistoryBtIntentServer");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        JSONObject msgObject = JSON.parseObject(intent.getStringExtra(DataSourceUpdateModule.EXTRA_UPDATE_OBJ));
        JSONObject content = msgObject.getJSONObject("content");
        int page, pageSize, desc;
        String keyword;
        if (content.containsKey("page")) {
            page = content.getInteger("page");
        } else {
            page = 1;
        }

        if (content.containsKey("pageSize")) {
            pageSize = content.getInteger("pageSize");
        } else {
            pageSize = 10;
        }

        if (content.containsKey("desc")) {
            desc = content.getInteger("desc");
        } else {
            desc = 1;
        }

        if (content.containsKey("keyword")) {
            keyword = content.getString("keyword");
        } else {
            keyword = "";
        }

        // 存储Service
        Service service = DataSourceUpdateModule.getServiceFromJson(msgObject);
        String downloadKey = DownloadManager.getDownloadKey();
        service.setDownloadKey(downloadKey);
//        DBManager.getDaoSession().getServiceDao().insert(service);
        ServiceHelper.getInstance().insertOrRelease(service);

        List<BroadcastTable> list = DBManager.getDaoSession().getBroadcastTableDao().queryBuilder()
                .where(BroadcastTableDao.Properties.Content.like("%" + keyword + "%"))
                .orderCustom(BroadcastTableDao.Properties.ModifyDate, desc == 0 ? "ASC" : "DESC")
                .offset(pageSize * (page - 1))
                .limit(pageSize)
                .list();

        JSONArray responseContent = new JSONArray();
        for (BroadcastTable bt : list) {
            responseContent.add(DataSourceUpdateModule.getBtContent(bt));
        }

        ReportMsg reportMsg = new ReportMsg(1, responseContent, downloadKey);
        EventBus.getDefault().post(reportMsg);
    }

}
