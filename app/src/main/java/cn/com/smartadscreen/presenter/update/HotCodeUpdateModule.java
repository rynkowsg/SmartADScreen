package cn.com.smartadscreen.presenter.update;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ZipUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import cn.com.smartadscreen.locallog.entity.HotInMsg;
import cn.com.smartadscreen.model.bean.DownloadFinished;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.service.HotInIntentService;
import cn.com.smartadscreen.presenter.service.HotOutIntentService;


/**
 * Created by Taro on 2017/3/13.
 * Hot Code Update 模块主入口
 */
public class HotCodeUpdateModule {

    public static final String TAG = "HotCodeUpdateModule";

    public static final String EXTRA_ASSET_FILE_PATH = "EXTRA_ASSET_FILE_PATH";
    public static final String EXTRA_UPDATE_TYPE = "EXTRA_UPDATE_TYPE";
    public static final String EXTRA_UPDATE_OBJ = "EXTRA_UPDATE_OBJ";

    public static final String UPDATE_TYPE_OS = "UPDATE_TYPE_OS";
    public static final String UPDATE_TYPE_FILE = "UPDATE_TYPE_FILE";

    private static HotCodeUpdateModule instance ;

    private static HotCodeUpdateModule newInstance() {
        if (instance == null) {
            instance = new HotCodeUpdateModule();
            if (!EventBus.getDefault().isRegistered(instance)) {
                EventBus.getDefault().register(instance);
            }
        }
        return instance;
    }

    public static boolean init(){
        boolean isUpgrade = false;
        int savedVersionCode = SPManager.getManager().getInt(SPManager.KEY_APP_VERSION_CODE);
        int currentVersionCode = AppUtils.getAppVersionCode(Config.getContext().getPackageName());
        if (savedVersionCode != -1) {
            if (currentVersionCode > savedVersionCode) {
                isUpgrade = true ;
            }
        } else {
            isUpgrade = true ;
        }
        SPManager.getManager().put(SPManager.KEY_APP_VERSION_CODE, currentVersionCode);

        if (instance == null) {
            HotCodeUpdateModule.newInstance();
        }

        return isUpgrade ;
    }

    public static void doUpgrade(){

        String filePath = "www.zip";
        Context context = Config.getContext();
        Intent intent = new Intent(context, HotOutIntentService.class);
        intent.putExtra(EXTRA_ASSET_FILE_PATH, filePath);
        context.startService(intent);

    }

    public static void doWebUpdate(JSONObject msgObject){

        Context context = Config.getContext();
        Intent intent = new Intent(context, HotInIntentService.class);
        intent.putExtra(EXTRA_UPDATE_TYPE, UPDATE_TYPE_OS);
        intent.putExtra(EXTRA_UPDATE_OBJ, msgObject.toString());
        context.startService(intent);

    }

    public static void doWebFileUpdate(JSONObject msgObject){

        Context context = Config.getContext();
        Intent intent = new Intent(context, HotInIntentService.class);
        intent.putExtra(EXTRA_UPDATE_TYPE, UPDATE_TYPE_FILE);
        intent.putExtra(EXTRA_UPDATE_OBJ, msgObject.toString());
        context.startService(intent);

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDownloadFinished(DownloadFinished download){
        if (download.getTag().equals(TAG)) {
            try {
                String wwwZipFilePath =
                        SPManager.getManager().getString(SPManager.KEY_APP_WEB_FOLDER) + "/www.zip";
                File wwwZipFile = new File(wwwZipFilePath);
                if (wwwZipFile.exists()) {
                    ZipUtils.unzipFile(wwwZipFilePath, SPManager.getManager().getString(SPManager.KEY_APP_WWW_FOLDER));
                    FileUtils.deleteFile(wwwZipFile);
                }
                EventBus.getDefault().post(new HotInMsg());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
