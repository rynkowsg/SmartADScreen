package cn.com.smartadscreen.presenter.nmc;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.facebook.stetho.common.LogUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import cn.com.smartadscreen.eventbus.bean.Event;
import cn.com.smartadscreen.eventbus.utils.EventBusUtils;
import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.model.bean.BroadcastTableDeleteBean;
import cn.com.smartadscreen.model.bean.DeviceInfoBean;
import cn.com.smartadscreen.model.bean.DownloadProgressBean;
import cn.com.smartadscreen.model.bean.ReportErrorMsg;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.bean.event.HardwareKey;
import cn.com.smartadscreen.model.bean.event.OnCommand;
import cn.com.smartadscreen.model.bean.event.OnLoadFinished;
import cn.com.smartadscreen.model.bean.event.OnScreenshot;
import cn.com.smartadscreen.model.db.entity.DownloadTable;
import cn.com.smartadscreen.model.db.manager.DownloadTableHelper;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.manager.DownloadManager;
import cn.com.smartadscreen.presenter.service.PluginServer;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;
import cn.com.smartadscreen.presenter.update.HotCodeUpdateModule;
import cn.com.smartadscreen.utils.JSONUtils;
import cn.startai.apkcommunicate.CommunicateType;
import cn.startai.apkcommunicate.StartaiCommunicate;
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
        else if (CommunicateType.COMMUNICATE_TYPE_DEVICE_INFOMATION.equals(flag)) {
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
        // Nmc 返回替换播表 file 字段
        else if (CommunicateType.COMMUNICATE_TYPE_PLAYLIST_DISTRIBUTE_1.equals(flag)) {
            // record
            ArrayList<String> remarks = new ArrayList<>();
            remarks.add("msg: " + msg);
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "NMC", "Native", "替换播表 File 字段", remarks));
            DataSourceUpdateModule.doReplaceBroadcastTable(msg);

        }

        // NMC 返回 MIOF 协议
        else if (CommunicateType.COMMUNICATE_TYPE_MIOF.equals(flag)) {

            boolean isHandled = false;
            // msg confirm
            JSONObject msgConfirm = new JSONObject();

            // record remark
            ArrayList<String> remarks = new ArrayList<>();
            // get object
            JSONObject msgObject = JSON.parseObject(msg);

            remarks.add("message:"+msg);
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "Native", "Native", "最开始接收到播表消息", remarks));
            String msgType = msgObject.getString("msgtype");
            try {
                remarks.add("data: " + msgObject.getJSONObject("content").toString());
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("JSON解析错误: " + e.getMessage());
                // 回复云端，json解析错误

                ReportErrorMsg errorMsg = new ReportErrorMsg();
                errorMsg.setResult(0);
                errorMsg.setMsgcw(NmcReport.getReportMsgcw(msgObject.getString("msgcw")));
                errorMsg.setToid(msgObject.getString("fromid"));
                errorMsg.setTs(System.currentTimeMillis());
                errorMsg.setMsgtype(msgType);
                JSONObject content = new JSONObject();
                content.put("msg", "JSON解析错误");
                errorMsg.setContent(content);
                EventBus.getDefault().post( errorMsg );

                return;
            } finally {
                //回复 nmc 已收到消息
                msgConfirm.put("msgType", msgType);
                StartaiCommunicate.getInstance().send(context,
                        CommunicateType.COMMUNICATE_TYPE_MIOF_CONFIRM,
                        msgConfirm.toString());
            }

            LogUtil.d("live", " msgType : " + msgType);
            Logger.i("指令==>" + msgType);
            Logger.json(msgObject.toJSONString());

            LogMsg logMsg = new LogMsg(LogMsg.TYPE_RECEIVED, "NMC", "Native", "", remarks);

            // 0x8500 更新 web 系统
            if ("0x8500".equals(msgType)) {
                isHandled = true;
                // record
                logMsg.setContent("更新 web 系统");
                SmartLocalLog.writeLog(logMsg);

                // handler
                HotCodeUpdateModule.doWebUpdate(msgObject);
            }


            // 0x8501 更新 web 文件
            else if ("0x8501".equals(msgType)) {
                isHandled = true;
                // record
                logMsg.setContent("更新 web 文件");
                SmartLocalLog.writeLog(logMsg);

                // handler
                HotCodeUpdateModule.doWebFileUpdate(msgObject);
            }

            // 0x8502 播表消息
            else if ("0x8502".equals(msgType)) {
                isHandled = true;
                // record
                logMsg.setContent("更新播表消息");
                SmartLocalLog.writeLog(logMsg);

                // handler
                DataSourceUpdateModule.doDataUpdate(msgObject);
            }

            // 0x8506 推送文件
            else if ("0x8506".equals(msgType)) {
                isHandled = true;
                // record
                logMsg.setContent("云端推送文件消息");
                SmartLocalLog.writeLog(logMsg);

                // handler
                DataSourceUpdateModule.doFileUpdate(msgObject);
            }

            // 0x8508 云端获得播表列表
            else if ("0x8508".equals(msgType)) {
                isHandled = true;
                // record
                logMsg.setContent("云端获得播表历史列表");
                SmartLocalLog.writeLog(logMsg);

                // handler
                DataSourceUpdateModule.doSendHistoryBt(msgObject);

            }

            // 0x8509 云端指定播放指定ID的播表
            else  if ("0x8509".equals(msgType)) {
                isHandled = true;
                // record
                logMsg.setContent("云端播放指定ID播表");
                SmartLocalLog.writeLog(logMsg);

                // handler
                DataSourceUpdateModule.playActionBt(msgObject);
            }

            // 0x8512 云端删除指定ID的播表
            else if ("0x8512".equals(msgType)) {
                isHandled = true;
                // record
                logMsg.setContent("云端删除指定播表");
                SmartLocalLog.writeLog(logMsg);

                // handler
                DataSourceUpdateModule.doDelFiles(msgObject);
            }

            if (!isHandled) {
                // record
                logMsg.setContent("未处理 MIOF 消息, 向下传递!");
                SmartLocalLog.writeLog(logMsg);

                int code = Integer.valueOf(msgType.substring(msgType.indexOf("x") + 1));
                JSONObject messageContent = msgObject.getJSONObject("content");
                String reportId = msgObject.getString("fromid");
                PluginServer.sendPluginMessage(code, messageContent.toString(), reportId);
            }

        }

        // NMC 返回下载进度消息
        else if (CommunicateType.COMMUNICATE_TYPE_FS_DOWNLOAD.equals(flag)) {

            DownloadProgressBean bean = JSONUtils.parseObject(msg, DownloadProgressBean.class);

            if (bean.getErrorCode().length() == 0) {
                DownloadManager.onProgress(bean);
                //改变数据库中的进度
                boolean isSuccess = DownloadTableHelper.getInstance().changeProgress(bean);
                if (!isSuccess) {
                    DownloadTableHelper.getInstance().add(DownloadTable.convert(bean));
                    DownloadTableHelper.getInstance().changeProgress(bean);
                }
            } else {
                //现在出错
                DownloadManager.onError(bean);


                boolean isSuccess = DownloadTableHelper.getInstance().changeError(bean);
                if (!isSuccess) {
                    DownloadTableHelper.getInstance().add(DownloadTable.convert(bean));
                    DownloadTableHelper.getInstance().changeError(bean);
                }
            }

            //通知 DownloadActivity 界面，下载进度发生改变
            EventBus.getDefault().post(bean);
        }
        // NMC 返回实体按键消息
        else if (CommunicateType.COMMUNICATE_TYPE_KEY.equals(flag)) {

            boolean enabledHardwareKey = SPManager.getManager().getBoolean(SPManager.KEY_ENABLED_HARDWARE_KEY, true);
            if (enabledHardwareKey) {
                int key = JSON.parseObject(msg).getIntValue("keyCode");
                HardwareKey hardwareKey = new HardwareKey(key);

                EventBus.getDefault().post(hardwareKey);
            }
        }

        // NMC 请求截图
        else if (CommunicateType.COMMUNICATE_TYPE_SCREENSHOT.equals(flag)) {
            OnScreenshot onScreenshot = JSON.parseObject(msg, OnScreenshot.class);
            EventBus.getDefault().post(onScreenshot);
            Logger.i("NMC 截屏");
        }
        // NMC 语音指令   云端操控播表
        else if (CommunicateType.COMMUNICATE_TYPE_VOICE_CONTROL.equals(flag)) {
            OnCommand onCommand = JSON.parseObject(msg, OnCommand.class);
            if (onCommand.getCmd() == CommunicateType.COMMUNICATE_TYPE_KEY_PLAY) {
                new Handler().postDelayed(() -> EventBus.getDefault().post(onCommand), 1200);
            } else {
//                getTopStackState();
                EventBus.getDefault().post(onCommand);
            }
        }

        //NMC 删除播表
      else if (CommunicateType.COMMUNICATE_TYPE_DELETE_PLAYLIST.equals(flag)) {
            JSONObject delete = JSON.parseObject(msg);
            EventBus.getDefault().post(new BroadcastTableDeleteBean(delete.getString("id")));
        }

    }
//    public void getTopStackState(){
//        Activity topActivity = ActivityUtils.getTopActivity();
//        String localClassName = topActivity.getLocalClassName();
//        Log.i("NmcComm", "localClassName = " + localClassName);
//        if (localClassName.lastIndexOf(PhoneControlActivity.class.getSimpleName()) > 0) {
//            topActivity.finish();
//        }
//    }
}
