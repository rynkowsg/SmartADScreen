package cn.com.smartadscreen.presenter.nmc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.com.smartadscreen.model.bean.ReportErrorMsg;
import cn.com.smartadscreen.model.bean.ReportMsg;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.db.entity.BroadcastTable;
import cn.com.smartadscreen.model.db.entity.Service;
import cn.com.smartadscreen.model.db.gen.BroadcastTableDao;
import cn.com.smartadscreen.model.db.gen.ServiceDao;
import cn.com.smartadscreen.model.db.manager.DBManager;
import cn.com.smartadscreen.utils.JSONUtils;
import cn.startai.apkcommunicate.CommunicateType;
import cn.startai.apkcommunicate.StartaiCommunicate;

/**
 * Created by Taro on 2017/3/24.
 * Nmc 回信类
 */
public class NmcReport {

    private static NmcReport instance ;
    private static Service service;

    private NmcReport(){
        EventBus.getDefault().register(this);
    }

    public synchronized static NmcReport newInstance(){
        synchronized (NmcReport.class) {
            if (instance == null) {
                instance = new NmcReport();
            }
            return instance ;
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void sendReport(ReportMsg reportMsg){
        if (service != null && service.getDownloadKey().equals(reportMsg.getDownloadKey())) {

        } else {
            service = DBManager.getDaoSession().getServiceDao().queryBuilder()
                    .where(ServiceDao.Properties.DownloadKey.eq(reportMsg.getDownloadKey()))
                    .orderDesc(ServiceDao.Properties.Id)
                    .limit(1)
                    .unique();
        }

        if (reportMsg.getContent() != null && reportMsg.getContent().containsKey("downloadKey")) {
            String downloadKey = reportMsg.getContent().getString("downloadKey");
            BroadcastTable unique = DBManager.getDaoSession().getBroadcastTableDao()
                    .queryBuilder()
                    .where(BroadcastTableDao.Properties.DownloadKey.eq(downloadKey))
                    .unique();
            if (unique != null) {
                reportMsg.getContent().put("id", unique.getBtId());
            }
        }

        if (service != null) {
            JSONObject serviceJson = (JSONObject) JSON.toJSON(service);
            serviceJson.put("requestId", service.getRequestId());
            serviceJson.put("method", service.getMethod());
            serviceJson.put("code", reportMsg.getCode());
            serviceJson.put("error", reportMsg.getError());
            serviceJson.put("params", reportMsg.getContent() == null ? reportMsg.getContentArray() : reportMsg.getContent());

            StartaiCommunicate.getInstance().send(Config.getContext(),
                    CommunicateType.COMMUNICATE_TYPE_MIOF, serviceJson.toString());

//            if (reportMsg.getResult() == 1) {
//                DBManager.getDaoSession().getServiceDao().delete(service);
//                service = null;
//            }
        }


    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void sendErrorReport(ReportErrorMsg errorMsg){

        StartaiCommunicate.getInstance().send(Config.getContext(),
                CommunicateType.COMMUNICATE_TYPE_MIOF, JSONUtils.toJSONString(errorMsg));

    }

    public static String getReportMsgcw(String requestMsgcw){
        if(requestMsgcw == null){
            requestMsgcw = "";
        }
        String msgcw = "";
        switch (requestMsgcw) {
            case "0x01":
                msgcw = "0x02";
                break;
            case "0x03":
                msgcw = "0x04";
                break;
            case "0x05":
                msgcw = "0x06";
                break;
            case "0x07":
                msgcw = "0x08";
                break;
            case "0x09":
                msgcw = "0x10";
                break;
            default:
                break;
        }
        return msgcw;
    }

}
