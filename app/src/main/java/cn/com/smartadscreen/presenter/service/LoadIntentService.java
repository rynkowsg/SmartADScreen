package cn.com.smartadscreen.presenter.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.common.LogUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.db.manager.DBManager;
import cn.com.smartadscreen.presenter.broadcast.CommandReceiver;
import cn.com.smartadscreen.presenter.crash.UnCeHandler;
import cn.com.smartadscreen.presenter.nmc.NmcCommunicateLogic;
import cn.com.smartadscreen.presenter.nmc.NmcReport;
import cn.com.startai.smartadh5.BuildConfig;
import cn.startai.apkcommunicate.CommunicateType;
import cn.startai.apkcommunicate.StartaiCommunicate;

/**
 * Created by Taro on 2017/3/17.
 * 执行应用初始化工作
 */
public class LoadIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LoadIntentService(String name) {
        super(name);
    }

    public LoadIntentService(){
        this("LoadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // 初始化全局配置类
        Config.setup();

        // 初始化调试工具
        if(BuildConfig.LOG_DEBUG) {
            Stetho.initializeWithDefaults(this.getApplicationContext());
            Log.i("LoadIntent","666666666");
        }

        // 初始化异常捕捉日志
        Thread.setDefaultUncaughtExceptionHandler(new UnCeHandler(this.getApplicationContext()));


//        GSYVideoType.enableMediaCodec();
//        GSYVideoType.enableMediaCodecTexture();

//        // 初始化Log模块
//        Logger.init("SmartADH5_LOG");

        // 初始化DB模块
        DBManager.setupDatabase();
        LogUtil.d("LoadIntent","db start");


//        SPManager.setupPreferences();

        // 初始化与 Nmc 的沟通类
        if(!StartaiCommunicate.isInit()){
            StartaiCommunicate.getInstance().init(this.getApplicationContext(), 1, new NmcCommunicateLogic());
        }
        NmcReport.newInstance();
        // 请求 SDCard 路径
        StartaiCommunicate.getInstance().send(this.getApplicationContext(), CommunicateType.COMMUNICATE_TYPE_SDCARD_PATH, "");
        // 请求设备 SN
        StartaiCommunicate.getInstance().send(this.getApplicationContext(), CommunicateType.COMMUNICATE_TYPE_DEVICE_INFOMATION, "");

        // 开启服务
        ServerManager.setupServer(this.getApplicationContext());


        // 注册接收指令的 Broadcast
        CommandReceiver commandReceiver = new CommandReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Config.ACTION_ENABLED_HARDWARE_KEY);
        intentFilter.addAction(Config.ACTION_SHOW_DOWNLOAD_PROGRESS);
        intentFilter.addAction(Config.ACTION_APP_USE_CROSSWALK);
        this.getApplicationContext().registerReceiver(commandReceiver, intentFilter);

        QueryBuilder.LOG_SQL = BuildConfig.LOG_DEBUG ;
        QueryBuilder.LOG_VALUES = BuildConfig.LOG_DEBUG;

    }

}
