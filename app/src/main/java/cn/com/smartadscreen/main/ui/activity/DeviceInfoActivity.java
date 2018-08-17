package cn.com.smartadscreen.main.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.smartadscreen.app.Application;
import cn.com.smartadscreen.model.bean.DeviceInfoBean;
import cn.com.smartadscreen.model.db.entity.BroadcastTable;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;
import cn.com.smartadscreen.utils.TimerUtils;
import cn.com.startai.smartadh5.R;

import cn.startai.apkcommunicate.CommunicateType;
import cn.startai.apkcommunicate.StartaiCommunicate;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static cn.com.smartadscreen.model.bean.config.Config.SETTING_TIME_OUT;


/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/16
 * 作用：
 */

public class DeviceInfoActivity extends AppCompatActivity {

    //销毁当前 Activity 的任务的key
    private final String KEY_FINISH_TASK = "DeviceInfoActivity.task.finish";
    private final String CURRENT_PLAY_NAME = "当前播放内容：%s";

    @BindView(R.id.tv_play_name)
    TextView tvPlayName;
    @BindView(R.id.tv_device_info)
    TextView tvDeviceInfo;
    @BindView(R.id.tc_time)
    TextClock tcTime;
    @BindView(R.id.tv_screen_time_mon)
    TextView tvScreenTimeMon;
    @BindView(R.id.tv_screen_time_tue)
    TextView tvScreenTimeTue;
    @BindView(R.id.tv_screen_time_wed)
    TextView tvScreenTimeWed;
    @BindView(R.id.tv_screen_time_thu)
    TextView tvScreenTimeThu;
    @BindView(R.id.tv_screen_time_fir)
    TextView tvScreenTimeFir;
    @BindView(R.id.tv_screen_time_sat)
    TextView tvScreenTimeSat;
    @BindView(R.id.tv_screen_time_sun)
    TextView tvScreenTimeSun;
    @BindView(R.id.tv_device_on_off_time_mon)
    TextView tvDeviceOnOffTimeMon;
    @BindView(R.id.tv_device_on_off_time_tue)
    TextView tvDeviceOnOffTimeTue;
    @BindView(R.id.tv_device_on_off_time_wed)
    TextView tvDeviceOnOffTimeWed;
    @BindView(R.id.tv_device_on_off_time_thu)
    TextView tvDeviceOnOffTimeThu;
    @BindView(R.id.tv_device_on_off_time_fir)
    TextView tvDeviceOnOffTimeFir;
    @BindView(R.id.tv_device_on_off_time_sat)
    TextView tvDeviceOnOffTimeSat;
    @BindView(R.id.tv_device_on_off_time_sun)
    TextView tvDeviceOnOffTimeSun;

    private String apkVersionName;
    private String format24Hour = "系统时间：yyyy-MM-dd HH:mm:ss";
    private Disposable disposable;

    private boolean isPlanInitSuccess = false;//开关屏安排 和 开关机安排是否已经初始化过，防止多次设置值

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_device_info);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        isPlanInitSuccess = false;
        initPlayName();
        initTimeClock();
        disposable = Observable.interval(0, 10, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong ->
                        StartaiCommunicate.getInstance().send(Application.getContext(), CommunicateType.COMMUNICATE_TYPE_DEVICE_INFOMATION, "")
                );
        apkVersionName = AppUtils.getAppVersionName();

    }

    private void initPlayName() {
        BroadcastTable currentBt = DataSourceUpdateModule.getCurrentBt();
        String playName;
        if (currentBt != null) {
            playName = currentBt.getName();
        } else {
            playName = "";
        }
        if (!TextUtils.isEmpty(playName)) {
            tvPlayName.setText(getString(R.string.play_broadcast_name, playName));
            tvPlayName.setVisibility(View.VISIBLE);
        } else {
            tvPlayName.setVisibility(View.GONE);
        }
    }

    private void initTimeClock() {
        if (Build.VERSION.SDK_INT == 17) {
            if (format24Hour.contains("HH")) {
                format24Hour = format24Hour.replace("HH", "hh");
                format24Hour += " a";
            }
        }
        tcTime.setFormat24Hour(format24Hour);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoSuccess(DeviceInfoBean bean) {
        String info = "终端版本：" + apkVersionName + "\n" +
                "终端sn：" + bean.getSn() + "\n" +
                "MAC地址：" + bean.getMac() + "\n" +
                "设备在线状态：" + bean.getConnectStatusString() + "\n" +
                "设备类型：" + bean.getMachineTypeString() + "\n" +
                "内网IP：" + bean.getIp() + "\n" +
                "外网IP：" + bean.getOutterIp() + "\n" +
                "网络连接方式：" + bean.getNetworkType() + "\n" +
                "分辨率：" + bean.getScreenSize() + "\n" +
                "CPU使用率：" + bean.getCpu() + "\n" +
                "内存使用：" + bean.getRam() + "\n" +
                "存储状态：" + bean.getRom() + "\n" +
                "系统运行时间：" + millToString(bean.getSystemRunningTime());

        tvDeviceInfo.setText(info);
        if (tvDeviceInfo.getVisibility() != View.VISIBLE) {
            tvDeviceInfo.setVisibility(View.VISIBLE);
        }

        if(!isPlanInitSuccess) {
            try {
                List<DeviceInfoBean.NormalPlans> screenNormalPlans = bean.getScreenNormalPlans();
                tvScreenTimeMon.setText(getPlanTime(screenNormalPlans.get(0)));
                tvScreenTimeTue.setText(getPlanTime(screenNormalPlans.get(1)));
                tvScreenTimeWed.setText(getPlanTime(screenNormalPlans.get(2)));
                tvScreenTimeThu.setText(getPlanTime(screenNormalPlans.get(3)));
                tvScreenTimeFir.setText(getPlanTime(screenNormalPlans.get(4)));
                tvScreenTimeSat.setText(getPlanTime(screenNormalPlans.get(5)));
                tvScreenTimeSun.setText(getPlanTime(screenNormalPlans.get(6)));

                List<DeviceInfoBean.NormalPlans> deviceNormalPlans = bean.getDeviceNormalPlans();
                tvDeviceOnOffTimeMon.setText(getPlanTime(deviceNormalPlans.get(0)));
                tvDeviceOnOffTimeTue.setText(getPlanTime(deviceNormalPlans.get(1)));
                tvDeviceOnOffTimeWed.setText(getPlanTime(deviceNormalPlans.get(2)));
                tvDeviceOnOffTimeThu.setText(getPlanTime(deviceNormalPlans.get(3)));
                tvDeviceOnOffTimeFir.setText(getPlanTime(deviceNormalPlans.get(4)));
                tvDeviceOnOffTimeSat.setText(getPlanTime(deviceNormalPlans.get(5)));
                tvDeviceOnOffTimeSun.setText(getPlanTime(deviceNormalPlans.get(6)));
            } catch (IndexOutOfBoundsException ignored) {
            }
            isPlanInitSuccess = true;
        }

    }

    private String getPlanTime(DeviceInfoBean.NormalPlans plans) {
        StringBuilder planTime = new StringBuilder();

        for (DeviceInfoBean.NormalPlans.PlanTime time : plans.getTiming()) {
            planTime.append(time.getStartTime())
                    .append("~")
                    .append(time.getEndTime())
                    .append("\n");
        }

        if(planTime.length() == 0) {
            return "无";
        }

        return planTime.toString();
    }

    private String millToString(long mill) {
        long allSecond = mill / 1000;
        long hour = allSecond / 3600;
        long minute = allSecond % 3600 / 60;
        long second = allSecond % 60;
        String hourStr = (hour < 10 ? "0" : "") + hour;
        String minuteStr = (minute < 10 ? "0" : "") + minute;
        String secondStr = (second < 10 ? "0" : "") + second;
        return hourStr + ":" + minuteStr + ":" + secondStr;
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
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
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
            //该界面自动关闭时，将打开此界面的设置界面同时关闭
            runOnUiThread(() -> {
                setResult(RESULT_OK);
                DeviceInfoActivity.this.finish();
            });
        }
    }
}
