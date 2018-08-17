package cn.com.smartadscreen.main.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.smartadscreen.app.Application;
import cn.com.smartadscreen.main.ui.base.BaseActivityV1;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.utils.TimerUtils;
import cn.com.startai.smartadh5.R;

import static cn.com.smartadscreen.model.bean.config.Config.SETTING_TIME_OUT;


/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/27
 *         作用：其他设置页买
 */

public class OtherSettingActivity extends BaseActivityV1 {
    @BindView(R.id.btn_webview_layer_type)
    Button btnWebViewLayerType;
    @BindView(R.id.btn_fixed_viewpager)
    Button btnFixedViewpager;
    @BindView(R.id.btn_switch_sync_type)
    Button btnSwitchSyncType;
    @BindView(R.id.sw_enabled_send_smart_load)
    SwitchCompat swEnabledSendSmartLoad;
    @BindView(R.id.sw_enabled_send_plugin_message)
    SwitchCompat swEnabledSendPluginMessage;
    @BindView(R.id.sw_enabled_receive_plugin_message)
    SwitchCompat swEnabledReceivePluginMessage;

    private int layerType;
    private int fixedPager;
    private int syncType;
    private boolean enabledSendSmartLoad;
    private boolean enabledSendPluginMessage;
    private boolean enabledReceivePluginMessage;

    //销毁当前 Activity 的任务的key
    private final String KEY_FINISH_TASK = "OtherSettingActivity.task.finish";

    public static void showActivityForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, OtherSettingActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_secondary_setup;
    }

    @Override
    public void initData() {
        layerType = SPManager.getInstance().getWebViewLayerType();
        fixedPager = SPManager.getInstance().getFixedViewpagerIndex();
        syncType = SPManager.getInstance().getSyncType();
        enabledSendSmartLoad = SPManager.getInstance().getEnabledSendSmartadLoad();
        enabledSendPluginMessage = SPManager.getInstance().getEnabledSendPluginMessage();
        enabledReceivePluginMessage = SPManager.getInstance().getEnabledReceivePluginMessage();
    }

    @Override
    public void initView() {

        String text = "无加速";
        switch (layerType) {
            case 1:
                text = "软件加速";
                break;
            case 2:
                text = "硬件加速";
                break;
        }
        btnWebViewLayerType.setText(text);

        String fixedText = "无固定";
        switch (fixedPager) {
            case 0:
                fixedText = "Default (默认页面)";
                break;
            case 1:
                fixedText = "Web (播表页面)";
                break;
            case 2:
                fixedText = "Music (音乐页面)";
                break;
            case 3:
                fixedText = "File (文件页面)";
                break;
        }
        btnFixedViewpager.setText(fixedText);

        String syncTypeText = "";
        switch (syncType) {
            case 0:
                syncTypeText = "每小时同步";
                break;
//            case 1:
//                syncTypeText = "绝对时间同步";
//                break;
            case 1:
                syncTypeText = "不同步";
                break;
        }
        btnSwitchSyncType.setText(syncTypeText);
        swEnabledSendSmartLoad.setChecked(enabledSendSmartLoad);
        swEnabledSendPluginMessage.setChecked(enabledSendPluginMessage);
        swEnabledReceivePluginMessage.setChecked(enabledReceivePluginMessage);
    }

    @Override
    public void addViewListener() {
        swEnabledSendSmartLoad.setOnCheckedChangeListener((buttonView, isChecked)
                -> SPManager.getInstance().saveEnabledSendSmartadLoad(isChecked)
        );

        swEnabledSendPluginMessage.setOnCheckedChangeListener((buttonView, isChecked)
                -> SPManager.getInstance().saveEnabledSendPluginMessage(isChecked)
        );

        swEnabledReceivePluginMessage.setOnCheckedChangeListener((buttonView, isChecked)
                -> SPManager.getInstance().saveEnabledReceivePluginMessage(isChecked));
    }

    @Override
    protected boolean shouldInterceptBack() {
        return false;
    }

    @OnClick(R.id.btn_webview_layer_type)
    public void setClickAppWebViewLayerType() {
        String[] items = {"无加速", "软件加速", "硬件加速"};
        new AlertDialog.Builder(this)
                .setTitle("请选择 WebView 的加速方式")
                .setSingleChoiceItems(items, layerType,
                        (dialog, which) -> {
                            btnWebViewLayerType.setText(items[which]);
                            SPManager.getInstance().saveWebViewLayerType(which);
                        })
                .setPositiveButton("确定", null)
                .show();
    }

    @OnClick(R.id.btn_fixed_viewpager)
    public void setFixedViewPager() {
        String[] items = {"Default (默认页面)", "Web (播表页面)", "Music (音乐页面)", "File (文件页面)", "无固定"};
        new AlertDialog.Builder(this)
                .setTitle("请选择需要固定的标签页")
                .setSingleChoiceItems(items, fixedPager == -1 ? 4 : fixedPager,
                        (dialog, which) -> {
                            btnFixedViewpager.setText(items[which]);
                            if (which == 4) {
                                which = -1;
                            }
                            SPManager.getManager().put(SPManager.KEY_FIXED_VIEWPAGER_INDEX, which);
                        })
                .setPositiveButton("确定", null)
                .show();
    }

    @OnClick(R.id.btn_switch_sync_type)
    public void setSyncType() {
        String[] items = getResources().getStringArray(R.array.sync_type);
        new AlertDialog.Builder(this)
                .setTitle("请选择同步方式")
                .setSingleChoiceItems(items, syncType,
                        (dialog, which) -> {
                            btnSwitchSyncType.setText(items[which]);
                            SPManager.getInstance().saveSyncType(which);
                        })
                .setPositiveButton("确定", null)
                .show();
    }

    @OnClick({R.id.btn_change_log, R.id.btn_restart_app})
    public void loadSmartAdChangeLog(View v) {
        switch (v.getId()) {
            case R.id.btn_change_log:
                //跳转至更新日志界面
                Intent intent = new Intent(this, ChangeLogActivity.class);
                this.startActivity(intent);
                break;
            case R.id.btn_restart_app:
                //重启应用
                new AlertDialog.Builder(this)
                        .setTitle("警告")
                        .setMessage("是否重启应用")
                        .setPositiveButton(R.string.ok, (dialog, which) -> {
//
                            Intent restartIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                            restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(restartIntent);


                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                break;
            default:
                break;
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
        EventBus.getDefault().unregister(this);
        //purge方法用于从这个计时器(Timer) 的任务队列中移除所有已取消的任务。
        Timer timer = TimerUtils.getTimerByKey(KEY_FINISH_TASK);
        if (timer != null) {
            timer.purge();
        }
        //检测泄露
        Application.getContext().mRefWatcher.watch(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //有按键操作时重新启动定时任务
        if(event.getAction() == KeyEvent.ACTION_DOWN)
            TimerUtils.schedule(KEY_FINISH_TASK, new FinishActivityTask(), SETTING_TIME_OUT);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //有触摸操作时重新启动定时任务
        if(ev.getAction() == MotionEvent.ACTION_DOWN)
            TimerUtils.schedule(KEY_FINISH_TASK, new FinishActivityTask(), SETTING_TIME_OUT);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * finish当前页面的任务
     */
    private class FinishActivityTask extends TimerTask {
        public void run() {
            //该界面自动关闭时，将打开此界面的设置界面同时关闭
            runOnUiThread(() -> {
                setResult(RESULT_OK);
                OtherSettingActivity.this.finish();
            });
        }
    }
}
