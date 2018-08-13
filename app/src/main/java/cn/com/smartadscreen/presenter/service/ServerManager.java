package cn.com.smartadscreen.presenter.service;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.util.HttpRequestParser;
import com.yanzhenjie.andserver.website.AssetsWebsite;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import cn.com.smartadscreen.model.bean.ServerRequestPage;
import cn.com.smartadscreen.model.sp.SPManager;


/**
 * Created by Taro on 2017/3/24.
 * 服务器管理类
 */
public class ServerManager {

    private static Server mServer;

    public static void setupServer(Context context) {
        if (mServer == null) {
            AndServer server = new AndServer.Build()
                    .port(8000)
                    .website(new AssetsWebsite(context.getAssets(), ""))
                    .registerHandler("switch", new RequestPageHandler())
                    .registerHandler("folder", new RequestFolderHandler())
                    .build();
            mServer = server.createServer();
            mServer.start();
        }
    }

    public static boolean isRunning(){
        return mServer != null && mServer.isRunning();
    }

    public static void start(){
        if(mServer != null) {
            mServer.start();
        }
    }

    public static class RequestPageHandler implements RequestHandler {

        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
            Map<String, String> params = HttpRequestParser.parse(request);

            // Request params.
            int direction = Integer.parseInt(params.get("direction"));

            EventBus.getDefault().post(new ServerRequestPage(direction));

        }
    }

    public static class RequestFolderHandler implements RequestHandler {

        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
            String baseFolder = SPManager.getInstance().getSdcardPath() + "/startai/";
            Map<String, String> params = HttpRequestParser.parse(request);
            String folder = params.get("folder");
            if (folder == null) {
                folder = baseFolder;
            } else {
                folder = baseFolder + folder;
            }

            JSONArray filesArray = new JSONArray();
            File file = new File(folder);
            if (file.exists()) {
                File[] files = file.listFiles();
                for (File childFile : files) {
                    if (childFile.isDirectory()) {
                        JSONObject dir = new JSONObject();
                        dir.put("type", "folder");
                        dir.put("name", childFile.getName());
                        filesArray.add(dir);
                    } else {
                        JSONObject f = new JSONObject();
                        f.put("type", "file");
                        f.put("name", childFile.getName());
                        f.put("size", FileUtils.getFileSize(childFile));
                        f.put("modifyDate", TimeUtils.millis2String(childFile.lastModified()));
                        filesArray.add(f);
                    }
                }
            }

            StringEntity resStr = new StringEntity(filesArray.toString(), "UTF-8");
            response.setEntity(resStr);
        }

    }
}
