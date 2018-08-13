package cn.com.smartadscreen.presenter.nmc;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.facebook.stetho.common.LogUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.model.bean.DeviceInfoBean;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.bean.event.OnLoadFinished;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;
import cn.com.smartadscreen.utils.JSONUtils;
import cn.startai.apkcommunicate.CommunicateType;
import cn.startai.apkcommunicate.StartaiCommunicateRecv;

public class NmcCommunicateLogic implements StartaiCommunicateRecv {


    @Override
    public void onReceive(Context context, String flag, String msg) {

        LogUtil.d("live", " NmcCommunicateLogic : " + flag);

        Logger.d("flag == " + flag + ";msg == " + msg);
        // NMC 返回 SDCard 路径
        if (CommunicateType.COMMUNICATE_TYPE_SDCARD_PATH.equals(flag)) {
            String sdCardPath = JSON.parseObject(msg).getString("sdcardPath");
            SPManager.getInstance().saveSdcardPath(sdCardPath);
            // record
            ArrayList<String> remarks = new ArrayList<>();
            remarks.add("sdcardPath: " + sdCardPath);
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "NMC", "Native", "获得SDCard路径", remarks));
        }
        // NMC 返回设备 设备信息
        if (CommunicateType.COMMUNICATE_TYPE_DEVICE_INFOMATION.equals(flag)) {
            DeviceInfoBean bean = JSONUtils.parseObject(msg, DeviceInfoBean.class);
            String sn = bean.getSn();
            String machineType = String.valueOf(bean.getMachineType());//机器类型
            int screenStatus = bean.getScreenStatus();//屏幕状态，1开0关
            Config.screenOff = (screenStatus == 0);
            if (bean.getGfInfo() != null) {
                Config.curBattery = bean.getGfInfo().getBattery();//当前电量
                Config.isBatteryCharging = bean.getGfInfo().getPower() == 1;//是否正在充电
            }
            String sdcardPath = bean.getSdcardPath();//sd路径
            String localIP = bean.getIp();//本机ip

            SPManager.getManager().put(SPManager.KEY_SCREEN_SHUT_OFF, Config.screenOff);
            SPManager.getInstance().saveSdcardPath(sdcardPath);
            SPManager.getInstance().saveLocalIP(localIP);
            //日志
            ArrayList<String> remarks = new ArrayList<>();
            remarks.add("sn: " + bean.getSn());
            remarks.add("clientId: " + bean.getClientId());
            remarks.add("machineType: " + machineType);
            remarks.add("screenStatus: " + screenStatus);
            remarks.add("sdcardPath: " + sdcardPath);
            remarks.add("本机ip: " + localIP);

            boolean isInitComplete = SPManager.getInstance().isInitSuccess();
            if (!isInitComplete) {
                SPManager.getInstance().saveIsMusicBox("1".equals(machineType));
                SPManager.getManager().put(SPManager.KEY_DEVICE_SN, sn);
                // record
                remarks.add("应用初始化");
                // 通知初始化完成
                EventBus.getDefault().post(new OnLoadFinished());
            }

            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "NMC", "Native", "获得设备信息", remarks));

            EventBus.getDefault().postSticky(bean);

        }
        // NMC 返回设备内存不足 清理缓存
        else if (CommunicateType.COMMUNICATE_TYPE_ROM_LIMIT.equals(flag)) {

            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "NMC", "Native", "设备内存不足, 清理data资源"));
            DataSourceUpdateModule.doClearData();

        }


    }
}
