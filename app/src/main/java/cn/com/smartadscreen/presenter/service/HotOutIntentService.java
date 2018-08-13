package cn.com.smartadscreen.presenter.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ZipUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.com.smartadscreen.model.bean.HotOutMsg;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.update.HotCodeUpdateModule;


public class HotOutIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public HotOutIntentService(String name) {
        super(name);
    }

    public HotOutIntentService(){
        this("HotOutIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            String filePath = intent.getStringExtra(HotCodeUpdateModule.EXTRA_ASSET_FILE_PATH);
            AssetManager assetManager = getApplicationContext().getAssets();
            InputStream in = assetManager.open(filePath);
            long fileSize = in.available();
            String destFilePath = getWebFolder() + filePath;//文件存放路径

            copyAssetFile(in, destFilePath, fileSize);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("www.zip file is required");
        }

    }

    private String getWebFolder(){
        String webFolder = SPManager.getManager().getString(SPManager.KEY_APP_WEB_FOLDER);
        if (!TextUtils.isEmpty(webFolder)) {
            File folderFile = new File(webFolder);
            if(!folderFile.exists()) {
                folderFile.mkdirs();
            }
            return webFolder;
        } else {
            String folderPath = SPManager.getInstance().getSdcardPath() + "/startai/web/";
            File folderFile = new File(folderPath);
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            SPManager.getManager().put(SPManager.KEY_APP_WEB_FOLDER, folderPath);
            return folderPath;
        }
    }

    private void copyAssetFile(InputStream in, String destinationFilePath, long fileSize) throws IOException {

        HotOutMsg msg = new HotOutMsg();
        msg.setStart(true);
        msg.setProgress(0);
        EventBus.getDefault().post(msg);

        long completeFileSize = 0;
        long sendProgress = 0;

        File destFile = new File(destinationFilePath);
        OutputStream out = new FileOutputStream(destFile);

        // Transfer bytes from in to out
        byte[] buf = new byte[8192];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
            completeFileSize += len ;
            long progress = completeFileSize * 100 / fileSize;
            if (progress - sendProgress > 10) {
                sendProgress = progress;
                msg.setProgress(progress);
                EventBus.getDefault().post(msg);
            }
        }

        in.close();
        out.close();

        msg.setProgressTip("准备执行解压操作...");
        EventBus.getDefault().post(msg);

        String wwwFolderPath = destFile.getParentFile().getAbsolutePath() + "/www";
        SPManager.getManager().put(SPManager.KEY_APP_WWW_FOLDER, wwwFolderPath);
        File wwwFolder = new File(wwwFolderPath);

        if (FileUtils.isFileExists(wwwFolder)) {
            FileUtils.deleteDir(wwwFolder);
        }

        ZipUtils.unzipFile(destFile, wwwFolder);
        FileUtils.deleteFile(destFile);

        String appLoadFilePath = "/startai/web/www/index.html";
        SPManager.getManager().put(SPManager.KEY_APP_LOAD_FILE_PATH, appLoadFilePath);

        msg.setFinish(true);
        msg.setProgress(100);
        EventBus.getDefault().post(msg);
    }
}
