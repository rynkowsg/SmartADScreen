package cn.com.smartadscreen.presenter.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.stetho.common.LogUtil;
import com.orhanobut.logger.Logger;

import cn.com.smartadscreen.model.db.entity.BroadcastTable;
import cn.com.smartadscreen.model.db.manager.DBManager;
import cn.startai.apkcommunicate.CommunicateType;
import cn.startai.apkcommunicate.StartaiCommunicate;

public class SendNeedReplaceFileIntentService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private SendNeedReplaceFileIntentService(String name) {
        super(name);
    }

    public SendNeedReplaceFileIntentService() {
        this("SendNeedReplaceFileIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long id = intent.getLongExtra("id", -1);

        LogUtil.d("live", " SendNeedReplaceFileIntentService onHandleIntent : "+ id);

        if (id != -1) {
            Logger.i("发送播表中需要替换的Url");
            BroadcastTable bt = DBManager.getDaoSession().getBroadcastTableDao().load(id);
            if (bt != null) {
                String btContent = bt.getContent();

                LogUtil.d("live", "handleContent before : "+ btContent);
                JSONArray content = handleContent(btContent);
                LogUtil.d("live", "handleContent after : "+ content.toString());

                Logger.i("计算需要替换的Url个数:" + content.size());
                LogUtil.d("live", "SendNeedReplaceFileIntentService计算需要替换的Url个数:" + content.size());

                JSONObject sendMessage = new JSONObject();
                sendMessage.put("id", bt.getId() + "");
                sendMessage.put("content", content);
                StartaiCommunicate.getInstance().send(getApplicationContext(),
                        CommunicateType.COMMUNICATE_TYPE_PLAYLIST_DISTRIBUTE_1,
                        sendMessage.toString());
            }
        }

        String receiveNmcReplaceContent = intent.getStringExtra("content");
        if (receiveNmcReplaceContent != null) {
            Logger.i("处理返回的需要替换的Url:" + receiveNmcReplaceContent);
            JSONObject handleReceiveContent = handleReceiveContent(receiveNmcReplaceContent);
            Logger.i("准备发送替换后的播表给Nmc: " + handleReceiveContent);

            LogUtil.d("live", "准备发送替换后的播表给Nmc: " + handleReceiveContent);

            if (handleReceiveContent != null) {
                StartaiCommunicate.getInstance().send(getApplicationContext(),
                        CommunicateType.COMMUNICATE_TYPE_PLAYLIST_DISTRIBUTE_2,
                        handleReceiveContent.toString());
            }
        }
    }

    private JSONObject handleReceiveContent(String receiveNmcReplaceContent) {
        JSONObject content = JSON.parseObject(receiveNmcReplaceContent);
        long id = Long.valueOf(content.getString("id"));
        JSONArray replaceArray = content.getJSONArray("content");

        BroadcastTable bt = DBManager.getDaoSession().getBroadcastTableDao().load(id);
        if (bt != null) {
            String btContent = bt.getContent();
            JSONObject btContentObject = JSON.parseObject(btContent);
            JSONObject contentObject = btContentObject.getJSONObject("content");
            JSONArray screens = contentObject.getJSONArray("screens");
            for (int i = 0, screenSize = screens.size(); i < screenSize; i++) {
                JSONObject screen = screens.getJSONObject(i);
                JSONArray apps = screen.getJSONArray("apps");
                for (int j = 0, appSize = apps.size(); j < appSize; j++) {
                    JSONObject app = apps.getJSONObject(j);
                    JSONArray items = app.getJSONArray("items");
                    for (int k = 0, itemSize = items.size(); k < itemSize; k++) {
                        JSONArray item = items.getJSONArray(k);
                        for (int l = 0, size = item.size(); l < size; l++) {
                            JSONObject itemItem = item.getJSONObject(l);
                            if (itemItem.containsKey("file") && itemItem.containsKey("hash")) {
                                for (int m = 0; m < replaceArray.size(); m++) {
                                    JSONObject replaceItem = replaceArray.getJSONObject(m);
                                    if (replaceItem.containsKey("hash")) {
                                        if (replaceItem.getString("hash").equals(itemItem.getString("hash"))) {
                                            itemItem.put("file", replaceItem.getString("url"));
                                        }
                                    } else {
                                        if (replaceItem.getString("name").equals(itemItem.getString("name"))) {
                                            itemItem.put("file", replaceItem.getString("url"));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return btContentObject;
        }
        return null;
    }

    private JSONArray handleContent(String content) {
        JSONArray sendContent = new JSONArray();

        JSONObject contentObject = JSON.parseObject(content);
        contentObject = contentObject.getJSONObject("content");
        JSONArray screens = contentObject.getJSONArray("screens");
        for (int i = 0, screenSize = screens.size(); i < screenSize; i++) {
            JSONObject screen = screens.getJSONObject(i);
            JSONArray apps = screen.getJSONArray("apps");
            for (int j = 0, appSize = apps.size(); j < appSize; j++) {
                JSONObject app = apps.getJSONObject(j);
                JSONArray items = app.getJSONArray("items");
                for (int k = 0, itemSize = items.size(); k < itemSize; k++) {
                    JSONArray item = items.getJSONArray(k);
                    for (int l = 0, size = item.size(); l < size; l++) {
                        JSONObject itemItem = item.getJSONObject(l);
                        if (itemItem.containsKey("file") && itemItem.containsKey("hash")) {
                            JSONObject contentItem = new JSONObject();
                            if (TextUtils.isEmpty(itemItem.getString("hash"))) {
                                contentItem.put("name", itemItem.getString("name"));
                            } else {
                                contentItem.put("hash", itemItem.getString("hash"));
                            }
                            contentItem.put("url", itemItem.getString("file"));
                            sendContent.add(contentItem);
                        }
                    }
                }
            }
    }

        return sendContent;
    }
}
