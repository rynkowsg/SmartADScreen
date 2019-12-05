//package cn.com.smartadscreen.presenter.service;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.orhanobut.logger.Logger;
//
//import cn.com.smartadscreen.model.bean.config.Config;
//import cn.com.smartadscreen.model.db.entity.Service;
//import cn.com.smartadscreen.model.sp.SPManager;
//import cn.com.smartadscreen.presenter.manager.PluginClient;
//import cn.startai.apkcommunicate.CommunicateType;
//import cn.startai.apkcommunicate.StartaiCommunicate;
//
///**
// * Created by Taro on 2017/4/5.
// * PluginServer 管理类
// */
//public class PluginServer {
//
//    // host loaded
//    private static final String ACTION_HOST_LOADED = "cn.com.startai.smartad.loaded";
//
//    // plugin
//    private static final String ACTION_SEND_PLUGIN_MESSAGE = "cn.com.startai.smartad.plugin.message";
//
//    private static PluginLoadReceiver pluginLoadReceiver;
//    private static PluginMessageReceiver pluginMessageReceiver;
//
//    public static void sendHostLoaded() {
//        boolean sendSmartLoad = SPManager.getManager().getBoolean(SPManager.KEY_ENABLED_SEND_SMARTAD_LOAD, true);
//        if (sendSmartLoad) {
//            Intent intent = new Intent(ACTION_HOST_LOADED);
//            JSONObject msgObject = new JSONObject();
//            msgObject.put("sn", SPManager.getManager().getString(SPManager.KEY_DEVICE_SN));
//            intent.putExtra(PluginClient.EXTRA_PLUGIN_MESSAGE_CONTENT, msgObject.toString());
//            Config.getContext().sendBroadcast(intent);
//        }
//
//
//        if (pluginLoadReceiver == null) {
//            pluginLoadReceiver = new PluginLoadReceiver();
//            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(PluginClient.ACTION_PLUGIN_LOAD);
//            Config.getContext().registerReceiver(pluginLoadReceiver, intentFilter);
//        }
//
//        if (pluginMessageReceiver == null) {
//            pluginMessageReceiver = new PluginMessageReceiver();
//            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(PluginClient.ACTION_PLUGIN_SEND_MESSAGE);
//            Config.getContext().registerReceiver(pluginMessageReceiver, intentFilter);
//        }
//    }
//
//    public static void sendPluginMessage(int code, String message, String reportId) {
//        boolean enabledSendPluginMessage = SPManager.getManager()
//                .getBoolean(SPManager.KEY_ENABLED_SEND_PLUGIN_MESSAGE, true);
//        if (enabledSendPluginMessage) {
//            Intent intent = new Intent(ACTION_SEND_PLUGIN_MESSAGE);
//            intent.putExtra(PluginClient.EXTRA_PLUGIN_MESSAGE_CODE, code);
//            intent.putExtra(PluginClient.EXTRA_PLUGIN_MESSAGE_CONTENT, message);
//            intent.putExtra(PluginClient.EXTRA_PLUGIN_MESSAGE_REPORT_ID, reportId);
//            Config.getContext().sendBroadcast(intent);
//            Logger.e("SmartAD -> Plugin:【" + code + "," + reportId + "," + message + "】");
//        }
//    }
//
//    public static void sendServerPause(boolean hide) {
//        boolean sendSmartLoad = SPManager.getManager()
//                .getBoolean(SPManager.KEY_ENABLED_SEND_SMARTAD_LOAD, true);
//        if (hide || sendSmartLoad) {
//            Intent intent = new Intent(PluginClient.ACTION_SERVER_PAUSE);
//            intent.putExtra("hide", hide);
//            Config.getContext().sendBroadcast(intent);
//        }
//    }
//
//    public static void handlePluginMessage(int code, String message, String reportId){
//        boolean enabledReceivePluginMessage = SPManager.getManager()
//                .getBoolean(SPManager.KEY_ENABLED_RECEIVE_PLUGIN_MESSAGE, true);
//        if (enabledReceivePluginMessage) {
//            Service service = new Service();
//            if (reportId != null) {
//                service.setToid(reportId);
//            } else {
//                service.setToid("$SERVICE/CDN/");
//            }
//            service.setResult(0);
//            service.setMsgtype("0x" + code);
//            service.setTs(System.currentTimeMillis());
//            service.setMsgcw("0x07");
//            service.setMsgid("");
//
//            JSONObject sendMessage = JSON.parseObject(JSON.toJSONString(service));
//            sendMessage.put("content", JSON.parseObject(message));
//
//            StartaiCommunicate.getInstance().send(Config.getContext(),
//                    CommunicateType.COMMUNICATE_TYPE_MIOF, sendMessage.toString());
//        }
//    }
//
//    private static class PluginLoadReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            PluginServer.sendHostLoaded();
//        }
//
//    }
//
//    private static class PluginMessageReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (PluginClient.ACTION_PLUGIN_SEND_MESSAGE.equals(intent.getAction())) {
//                int code = intent.getIntExtra(PluginClient.EXTRA_PLUGIN_MESSAGE_CODE, -1);
//                String message = intent.getStringExtra(PluginClient.EXTRA_PLUGIN_MESSAGE_CONTENT);
//                String reportId = intent.getStringExtra(PluginClient.EXTRA_PLUGIN_MESSAGE_REPORT_ID);
//                if (code != -1) {
//                    PluginServer.handlePluginMessage(code, message, reportId);
//                }
//            }
//        }
//
//    }
//
//}
