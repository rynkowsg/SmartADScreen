package cn.com.smartadscreen.app;

import android.support.multidex.MultiDexApplication;

import com.blankj.utilcode.util.Utils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import cn.com.startai.smartadh5.BuildConfig;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class Application extends MultiDexApplication {
    private static Application context ;
//    private AppComponent mAppComponent;
public static RefWatcher mRefWatcher ;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        //设置IJKPlayer 的日志等级为最大级别，以减少日志输出
        GSYVideoManager.instance().setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("SmartADH5_LOG")
                .methodCount(1)
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.LOG_DEBUG;
            }
        });
        //初始化泄漏工具
        mRefWatcher = LeakCanary.install(this);
        Utils.init(context);

    }

    public static Application getContext() {
        return context;
    }

//    public AppComponent getAppComponent() {
//        return mAppComponent;
//    }

//    private void init(){
//        mAppComponent=DaggerAppComponent.builder().appModule(new AppModule()).build();
//    }



}
