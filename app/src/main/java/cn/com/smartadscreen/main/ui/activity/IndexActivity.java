package cn.com.smartadscreen.main.ui.activity;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;

import com.blankj.utilcode.util.SPUtils;
import com.facebook.stetho.common.LogUtil;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xwalk.core.XWalkInitializer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.smartadscreen.app.Application;
import cn.com.smartadscreen.di.componets.DaggerIndexComponets;
import cn.com.smartadscreen.di.module.IndexModule;
import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.HotInMsg;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.main.ui.adapter.ViewPagerAdapter;
import cn.com.smartadscreen.main.ui.base.BaseActivityV1;
import cn.com.smartadscreen.main.ui.base.BaseFragment;
import cn.com.smartadscreen.main.ui.fragment.DefaultFragment;
import cn.com.smartadscreen.main.ui.fragment.WebFragment;
import cn.com.smartadscreen.main.ui.pager.VerticalViewPager;
import cn.com.smartadscreen.main.ui.view.SmartToast;
import cn.com.smartadscreen.model.bean.BroadcastTableDeleteBean;
import cn.com.smartadscreen.model.bean.OnVideoPlayer;
import cn.com.smartadscreen.model.bean.event.HardwareKey;
import cn.com.smartadscreen.model.bean.OnTimeoutExecute;
import cn.com.smartadscreen.model.bean.TaskPush;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.bean.event.OnCommand;
import cn.com.smartadscreen.model.bean.event.OnTextPlayer;
import cn.com.smartadscreen.model.bean.event.PlayModeChangedEvent;
import cn.com.smartadscreen.model.db.entity.BroadcastTable;
import cn.com.smartadscreen.model.db.entity.Screen;
import cn.com.smartadscreen.model.db.manager.BroadcastTableHelper;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.main.IndexPresenter;
import cn.com.smartadscreen.presenter.service.PluginServer;
import cn.com.smartadscreen.presenter.task.StateDate;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;
import cn.com.smartadscreen.utils.SmartTimerService;
import cn.com.startai.smartadh5.R;
import cn.startai.apkcommunicate.CommunicateType;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IndexActivity extends BaseActivityV1 implements XWalkInitializer.XWalkInitListener, IndexContract.View {
    @BindView(R.id.verticalViewPager)
    VerticalViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<BaseFragment> mFragments;

    private DefaultFragment mDefaultFragment;
    private WebFragment mWebFragment;


    private int fixedPager;
    private static boolean isBtStop = false;
    private int defaultPlayMode = 0;
    private String TAG = this.getClass().getSimpleName();
    @Inject
    IndexPresenter indexPresenter;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, IndexActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected boolean shouldInterceptBack() {
        SPUtils.getInstance().put("NextNumber", 1);
        return true;
    }

    @Override
    protected int getLayoutId() {
        if (SPManager.getInstance().isMusicBox()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        return R.layout.activity_index;
    }

    @Override
    public void initData() {


        Logger.i("执行oncreate");
        if (Config.screenOff) {
            isBtStop = true;
        }
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        if (SPManager.getManager().getBoolean(SPManager.KEY_APP_USE_CROSSWALK, false)) {
            new XWalkInitializer(this, this).initAsync();
        } else {
            //初始化fragment
            initFragments();
            Logger.i("已经初始化fragment");
        }
        //初始化dagger
        DaggerIndexComponets.builder()
                .indexModule(new IndexModule(this, mWebFragment, mFragments))
                .build()
                .inject(this);
        // SmartAD Loaded
        PluginServer.sendHostLoaded();


        SPUtils.getInstance().put("getFragmentPos", 5);

    }

    private void initFragments() {
        mFragments = new ArrayList<>();
        mWebFragment = new WebFragment();

        mWebFragment.setRetainInstance(true);

        mDefaultFragment = new DefaultFragment();
        mFragments.add(mDefaultFragment);

        //设置渲染方式为 SurfaceView
//        GSYVideoType.setRenderType(GSYVideoType.GLSURFACE);
        //设置开启 精准进度条
        VideoOptionModel videoOptionModel1 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        List<VideoOptionModel> list = new ArrayList<>();

        GSYVideoType.enableMediaCodec();
        GSYVideoType.enableMediaCodecTexture();
        //广告屏
        mFragments.add(mWebFragment);


        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LogUtil.v("indexActivity", "position = " + position);

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("indexActivity", "position=" + position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(mFragments.size());


        fixedPager = SPManager.getManager().getInt(SPManager.KEY_FIXED_VIEWPAGER_INDEX, -1);

        Logger.i("fixedPager" + fixedPager);
        if (fixedPager != -1) {
            mViewPager.setCurrentItem(fixedPager);
        } else {
            mViewPager.setCurrentItem(0);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.i("执行onResume");
        PluginServer.sendServerPause(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.i("执行onPause");
        PluginServer.sendServerPause(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name)) {
                    return getApplicationContext().getSystemService(name);
                }
                return super.getSystemService(name);
            }
        });

    }

    //------------------------------eventBus 接收-----------------------------------------------------

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskPush(TaskPush taskPush) {


//        doTaskPush(taskPush);

        indexPresenter.doTaskPushByPre(taskPush);
        Logger.i("onTaskPush");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeoutExecute(OnTimeoutExecute execute) {
        if (mWebFragment != null) {
            mWebFragment.onTimeout(execute.getTimeoutKey());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHotInFinished(HotInMsg hotInMsg) {
        if (mWebFragment != null) {
            mWebFragment.reload();
        }
    }

    //语音指令
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommand(OnCommand command) {
        switch (command.getCmd()) {
            case CommunicateType.COMMUNICATE_TYPE_KEY_BARCODE:
                mViewPager.setCurrentItem(0);
                if (mDefaultFragment != null) {
                    mDefaultFragment.switchPage(1);
                }
                break;
            case CommunicateType.COMMUNICATE_TYPE_KEY_ALARMCLOCK:
            case CommunicateType.COMMUNICATE_TYPE_KEY_PAUSE:
                mViewPager.setCurrentItem(0);
                if (mDefaultFragment != null) {
                    mDefaultFragment.switchPage(0);
                }
                break;
            case CommunicateType.COMMUNICATE_TYPE_KEY_PLAY:
                if (!SPManager.getInstance().isMusicBox()) {
                    if (mViewPager.getCurrentItem() != 1) {
                        mViewPager.setCurrentItem(1);
                    }
                } else {
                    mViewPager.setCurrentItem(SPManager.getInstance().getCurrentPlayerMode());
                }
                break;
            case CommunicateType.COMMUNICATE_TYPE_KEY_RECOGNIZETION_START:
                Intent intent = new Intent(this, VoiceActivity.class);
                startActivity(intent);
                break;
            case CommunicateType.COMMUNICATE_TYPE_KEY_STOP:
                if (!isBtStop) {
                    mViewPager.setCurrentItem(0);
                    indexPresenter.sendState(new StateDate(3));
                    LogUtil.v("indexAcitvity", "index 得sendstate执行了");
                    isBtStop = true;
                }
                break;
            case CommunicateType.COMMUNICATE_TYPE_KEY_REPLAY:
                DataSourceUpdateModule.playBtById("-1");
                break;
        }
    }

    /**
     * 删除播表
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteBroadTable(BroadcastTableDeleteBean bean) {
        List<BroadcastTable> tables = BroadcastTableHelper.getInstance().queryByBtId(bean.getId());

        //停止延时推送任务
        for (BroadcastTable table : tables) {
            List<Screen> screens = table.getScreens();
            for (Screen screen : screens) {
                SmartTimerService.getInstance().stopTask(String.valueOf(screen.getId()));
                SmartTimerService.getInstance().stopTask(String.valueOf(-screen.getId()));
            }
        }
        BroadcastTableHelper.getInstance().deleteBroadcastTable(tables);
        SmartToast.success("播表删除成功");
        LogMsg logMsg = new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native", "删除播表id为" + bean.getId() + "的播表");
        SmartLocalLog.writeLog(logMsg);

    }


    //按键指令
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHardwareKeyPress(HardwareKey hardwareKey) {
        if (!isForeground) {
            return;
        }
        int currentItem = mViewPager.getCurrentItem();
        int itemSize = mFragments.size();
        Log.d(TAG, "COMMUNICATE_TYPE_MEDIA_PLAYLIST_CHANGE    currentItem ==> " + currentItem);

        if (hardwareKey.getDirection() == CommunicateType.COMMUNICATE_TYPE_KEY_ACTION_DOWN
                && currentItem > 0) {
            if (fixedPager != -1) {
                SmartToast.error("标签页已经固定, 无法切换!");
            } else {
                mViewPager.setCurrentItem(currentItem - 1);
                EventBus.getDefault().postSticky(new PlayModeChangedEvent(currentItem - 1));
            }
        }

        if (hardwareKey.getDirection() == CommunicateType.COMMUNICATE_TYPE_KEY_ACTION_UP
                && currentItem < (itemSize - 1)) {
            if (fixedPager != -1) {
                SmartToast.error("标签页已经固定, 无法切换!");
            } else {
                mViewPager.setCurrentItem(currentItem + 1);
                EventBus.getDefault().postSticky(new PlayModeChangedEvent(currentItem + 1));
            }

        }

        if (hardwareKey.getDirection() == CommunicateType.COMMUNICATE_TYPE_KEY_ACTION_LEFT) {
            for (BaseFragment mFragment : mFragments) {
                if (mFragment.getVisible()) {
                    mFragment.onLeftPress();
                }
            }
        }

        if (hardwareKey.getDirection() == CommunicateType.COMMUNICATE_TYPE_KEY_ACTION_RIGHT) {
            for (BaseFragment mFragment : mFragments) {
                if (mFragment.getVisible()) {
                    mFragment.onRightPress();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoPalyer(OnVideoPlayer onVideoPlayer) {
        ArrayList<String> remark = new ArrayList<>();
        if (onVideoPlayer.getCancelIntent() != null && onVideoPlayer.getCancelIntent()) {
            // cancel video
            Logger.i("H5请求原生取消播放视频! videoId: " + onVideoPlayer.getVideoId() + " path: " + onVideoPlayer.getPath());
            remark.add("屏幕状态：" + (Config.screenOff ? "关屏" : "开屏"));
            remark.add("取消播放视频!");

            if (mWebFragment != null) {
                mWebFragment.onCancelVideo(onVideoPlayer.getVideoId(), true);
            }
        } else {

            remark.add("屏幕状态：" + (Config.screenOff ? "关屏" : "开屏"));
            remark.add("path: " + onVideoPlayer.getPath());

            if (!Config.screenOff) {
                remark.add("播放视频! ");
                Logger.i("H5请求原生播放视频! videoId: " + onVideoPlayer.getVideoId());
                if (mWebFragment != null) {
                    mWebFragment.onPlayVideo(onVideoPlayer);
                }
            } else {
                remark.add("屏幕关闭，不播放视频 ");
            }
        }

        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native"
                , "H5请求原生视频操作!", remark));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTextPlayer(OnTextPlayer onTextPlayer) {
        if (onTextPlayer.getCancelIntent() != null && onTextPlayer.getCancelIntent()) {
            // cancel text
            Logger.i("H5请求原生取消文字跑马灯！");

            if (mWebFragment != null) {
                mWebFragment.onCancelText(onTextPlayer.getTextId(), true);
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native"
                        , "H5请求原生取消文字跑马灯!"));
            }
        } else {
            // play text
            Logger.i("H5请求原生文字跑马灯！textContext: " + onTextPlayer.getContent());
            if (mWebFragment != null) {
                mWebFragment.onPlayText(onTextPlayer);
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native"
                        , "H5请求原生文字跑马灯!"));

                //记录跑马灯日志
                ArrayList<String> remarks = new ArrayList<>();
                remarks.add("跑马灯内容： " + Html.fromHtml(onTextPlayer.getContent()).toString());
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_RECEIVED, "HTML", "Native"
                        , "H5请求原生文字跑马灯!", remarks));
            }
        }
    }


    //---------------------------------------------------------------------------------------------------


    public static boolean isBtStop() {
        return isBtStop;
    }

    public static void setBtStop(boolean b) {
        isBtStop = b;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return SPManager.getManager().getBoolean(SPManager.KEY_ENABLED_TOUCH_EVENT, false)
                && super.dispatchTouchEvent(ev);
    }

    @Override
    public void initView() {

    }

    @Override
    public void addViewListener() {

    }

    @Override
    public void onXWalkInitStarted() {

    }

    @Override
    public void onXWalkInitCancelled() {

    }

    @Override
    public void onXWalkInitFailed() {
        SmartToast.error("CrossWalk 加载失败!");
        // CrossWalk 加载失败
        new AlertDialog.Builder(this)
                .setTitle("Error!")
                .setMessage("无法加载CrossWalk, 请检查配置环境!")
                .setCancelable(false)
                .setPositiveButton("确定", null)
                .show();
    }

    @Override
    public void onXWalkInitCompleted() {
        SmartToast.success("CrossWalk 加载成功!");
        // CrossWalk 加载成功 !
        initFragments();
    }

    @Override
    public void setCurrentItem(int item) {
        Logger.i("item"+item);

        mViewPager.setCurrentItem(item);
    }

    @Override
    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        indexPresenter.unRegister();
        //检测泄露
        Application.getContext().mRefWatcher.watch(this);
    }
}
