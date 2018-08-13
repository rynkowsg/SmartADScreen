package cn.com.smartadscreen.presenter.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.smartadscreen.model.sp.SPManager;

public class UniqueDataSourceFileIntentService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private UniqueDataSourceFileIntentService(String name) {
        super(name);
    }

    public UniqueDataSourceFileIntentService() {
        this("UniqueDataSourceFileIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        List<String> suffixList = Arrays.asList("imageplayer", "mediaplayer",
                "textplayer", "urlplayer", "musicplayer");

        String dataPath = SPManager.getInstance().getAppDataPath();
        File dataFile = new File(dataPath);
        File[] childFiles = dataFile.listFiles(file -> {
            return suffixList.contains(file.getPath().substring(file.getPath().lastIndexOf(".") + 1));
        });

        if (childFiles != null) {
            Map<String, String> childFileMap = new HashMap<>();
            for (File childFile : childFiles) {
                String fileValue = new String(FileIOUtils.readFile2BytesByStream(childFile));
                JSONObject fileObject = JSON.parseObject(fileValue);
                if (TextUtils.isEmpty(fileValue) || fileObject == null) {
                    Logger.i("空文件, 不符合要求, 直接删除");
                    FileUtils.deleteFile(childFile);
                    continue;
                }
                fileValue = fileObject.toString();
                if (fileObject.containsKey("path")) {
                    String path = fileObject.getString("path");
                    if (childFileMap.containsKey(path)) {
                        FileUtils.deleteFile(childFile);
                    } else {
                        childFileMap.put(path, fileValue);
                    }
                } else if (fileObject.containsKey("content")) {
                    String content = fileObject.getString("content");
                    if (childFileMap.containsKey(content)) {
                        FileUtils.deleteFile(childFile);
                    } else {
                        childFileMap.put(content, fileValue);
                    }
                } else if (fileObject.containsKey("url")) {
                    String url = fileObject.getString("url");
                    if (childFileMap.containsKey(url)) {
                        FileUtils.deleteFile(childFile);
                    } else {
                        childFileMap.put(url, fileValue);
                    }
                }

            }
        }

        Logger.i("去重完毕");
    }
}
