package cn.com.smartadscreen.main.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.smartadscreen.app.Application;
import cn.com.smartadscreen.di.componets.DaggerSetupComponets;
import cn.com.smartadscreen.di.module.SetupModule;
import cn.com.smartadscreen.main.ui.base.BaseActivityV1;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.main.SetupPresenter;
import cn.com.smartadscreen.utils.TimerUtils;
import cn.com.startai.smartadh5.R;
import static cn.com.smartadscreen.model.bean.config.Config.SETTING_TIME_OUT;

public class SetupActivity extends BaseActivityV1{
    @BindView(R.id.app_version)
    TextView appVersion;
    @BindView(R.id.enabled_hardware_key_switch)
    SwitchCompat mEnabledHardwareKeySwitch;//启用实体按键
    @BindView(R.id.enabled_toast_download_info_switch)
    SwitchCompat mEnabledToastDownloadInfoSwitch;//启用现在进度显示
    @BindView(R.id.app_use_crosswalk_switch)
    SwitchCompat mAppUseCrossWalkSwitch;
    @BindView(R.id.enabled_touch_event_switch)
    SwitchCompat mEnabledTouchEventSwitch;


    @Inject
    SetupPresenter setupPresenter;
    private final int REQUEST_TO_OTHER_PAGE = 0x01;
    //销毁当前 Activity 的任务的key
    private final String KEY_FINISH_TASK = "SetupPresenter.task.finish";




    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SetupActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected boolean shouldInterceptBack() {
        SPUtils.getInstance().put("NextNumber",1);
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setup;
    }

    @Override
    public void initData() {
        String appVersionName = AppUtils.getAppVersionName(getPackageName());
        appVersion.setText(appVersionName);
        //初始化dagger
        DaggerSetupComponets.builder()
                .setupModule(new SetupModule(this))
                .build()
                .inject(this);
        mEnabledHardwareKeySwitch.setChecked(setupPresenter.getValuesBySpManger(SPManager.KEY_ENABLED_HARDWARE_KEY));
        mEnabledToastDownloadInfoSwitch.setChecked(setupPresenter.getValuesBySpManger(SPManager.KEY_ENABLED_TOAST_DOWNLOAD_INFO));
        mAppUseCrossWalkSwitch.setChecked(setupPresenter.getValuesBySpManger(SPManager.KEY_APP_USE_CROSSWALK));
        mEnabledTouchEventSwitch.setChecked(setupPresenter.getValuesBySpManger(SPManager.KEY_ENABLED_TOUCH_EVENT));

    }

    @Override
    public void initView() {

    }

    @Override
    public void addViewListener() {
        mEnabledHardwareKeySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setupPresenter.putValuesBySpManger(SPManager.KEY_ENABLED_HARDWARE_KEY, isChecked));
        mEnabledToastDownloadInfoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setupPresenter.putValuesBySpManger(SPManager.KEY_ENABLED_TOAST_DOWNLOAD_INFO, isChecked));
        mAppUseCrossWalkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setupPresenter.putValuesBySpManger(SPManager.KEY_APP_USE_CROSSWALK, isChecked));
        mEnabledTouchEventSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setupPresenter.putValuesBySpManger(SPManager.KEY_ENABLED_TOUCH_EVENT, isChecked));
    }
    @OnClick({R.id.enabled_hardware_key, R.id.btn_device_info, R.id.btn_check_download, R.id.btn_jump_to_other_settings})
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.enabled_hardware_key:
                //启用实体按键
                boolean checked = mEnabledHardwareKeySwitch.isChecked();
                mEnabledHardwareKeySwitch.setChecked(!checked);
                break;
            case R.id.btn_device_info:
                //设备信息
                startActivityForResult(
                        new Intent(SetupActivity.this, DeviceInfoActivity.class), REQUEST_TO_OTHER_PAGE);
                break;
            case R.id.btn_check_download:
                //下载列表
                DownloadActivity.showActivityForResult(SetupActivity.this, REQUEST_TO_OTHER_PAGE);
                break;
            case R.id.btn_jump_to_other_settings:
                //下载列表
                OtherSettingActivity.showActivityForResult(SetupActivity.this, REQUEST_TO_OTHER_PAGE);
                break;
            default:
                break;
        }
    }


    @OnClick(R.id.enabled_toast_download_info)
    public void enabledToastDownloadInfo() {
        boolean checked = mEnabledToastDownloadInfoSwitch.isChecked();
        mEnabledToastDownloadInfoSwitch.setChecked(!checked);
    }

    @OnClick(R.id.app_use_crosswalk)
    public void setAppUseCrossWalk() {
        boolean checked = mAppUseCrossWalkSwitch.isChecked();
        mAppUseCrossWalkSwitch.setChecked(!checked);
    }

    @OnClick(R.id.enabled_touch_event)
    public void setEnabledTouchEvent() {
        boolean checked = mEnabledTouchEventSwitch.isChecked();
        mEnabledTouchEventSwitch.setChecked(!checked);
    }

    @OnClick(R.id.jump_to_nmc_setup)
    public void jumpToNmcSetup() {
        try {
            Intent intent = new Intent("action.startai.setting");
            intent.putExtra("index", 2);
            startActivity(intent);
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("打开 NMC 异常")
                    .setMessage("请安装新版 NMC 才可以开启 NMC 设置页面!")
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    @OnClick(R.id.jump_to_desktop)
    public void jumpToDesktop() {
        try {
            Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
            launcherIntent.addCategory(Intent.CATEGORY_HOME);
            startActivity(launcherIntent);
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("调起桌面程序异常")
                    .setMessage("不知道什么原因, 反正就是调不起来!")
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    @OnClick(R.id.jump_to_setup)
    public void jumpToSetup() {
        try {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("调起设置异常")
                    .setMessage("不知道什么原因, 反正就是调不起来!")
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TO_OTHER_PAGE:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开启定时任务，TIME_OUT 时间后关闭当前界面
        TimerUtils.schedule(KEY_FINISH_TASK, new FinishActivityTask(), SETTING_TIME_OUT);
        Logger.i("设置页面定时任务开启");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //关闭定时任务
        TimerUtils.close(KEY_FINISH_TASK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //purge方法用于从这个计时器(Timer) 的任务队列中移除所有已取消的任务。
        Timer timer = TimerUtils.getTimerByKey(KEY_FINISH_TASK);
        if (timer != null) {
            timer.purge();
        }
        Application.mRefWatcher.watch(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //有按键操作时重新启动定时任务
        if (event.getAction() == KeyEvent.ACTION_DOWN)
            TimerUtils.schedule(KEY_FINISH_TASK, new FinishActivityTask(), SETTING_TIME_OUT);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //有触摸操作时重新启动定时任务
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            TimerUtils.schedule(KEY_FINISH_TASK, new FinishActivityTask(), SETTING_TIME_OUT);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * finish当前页面的任务
     */
    private class FinishActivityTask extends TimerTask {
        public void run() {
            SPUtils.getInstance().put("NextNumber",1);
            SetupActivity.this.finish();
        }
    }


}
