package cn.com.smartadscreen.main.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.orhanobut.logger.Logger;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.main.ui.base.BaseActivity;
import cn.com.smartadscreen.model.bean.HotOutMsg;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.bean.event.OnLoadFinished;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.service.LoadIntentService;
import cn.com.smartadscreen.presenter.update.HotCodeUpdateModule;
import cn.com.smartadscreen.utils.DialogHelper;
import cn.com.smartadscreen.utils.SmartToast;
import cn.com.startai.smartadh5.R;


/**
 * 程序入口
 */
public class ExtranceActivity extends BaseActivity {

    @BindView(R.id.extrance_progress_bar)
    NumberProgressBar mProgressBar;
    @BindView(R.id.extrance_progress_bar_tip)
    TextView mProgressTip;
    @BindView(R.id.loading)
    AVLoadingIndicatorView mLoading;
    private String TAG = "ExtranceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extrance);
        ButterKnife.bind(this);

        //应用未初始化完成
        SPManager.getInstance().saveIsInitSuccess(false);
        // 开启 LoadService
        Intent intent = new Intent(this.getApplicationContext(), LoadIntentService.class);
        startService(intent);

//        PermissionUtils.permission(PermissionConstants.STORAGE)
//                .rationale(new PermissionUtils.OnRationaleListener() {
//                    @Override
//                    public void rationale(ShouldRequest shouldRequest) {
//                        Log.i(TAG,"rationale");
//                        DialogHelper.showRationaleDialog(shouldRequest);
//
//                    }
//                }).callback(new PermissionUtils.FullCallback() {
//            @Override
//            public void onGranted(List<String> permissionsGranted) {
//
//                Log.i(TAG,"onGranted");
//                toCopyAssetsFile();
//            }
//
//            @Override
//            public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
//                Log.i(TAG,"onDenied");
//                if (!permissionsDeniedForever.isEmpty()) {
//                    DialogHelper.showOpenAppSettingDialog();
//                }
//            }
//        }).request();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadFinished(OnLoadFinished load) {


        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            //anroid 6.0 以上的版本 动态申请权限
            PermissionUtils.permission(PermissionConstants.STORAGE)
                    .rationale(new PermissionUtils.OnRationaleListener() {
                        @Override
                        public void rationale(ShouldRequest shouldRequest) {
                            Log.i(TAG,"rationale");
                            DialogHelper.showRationaleDialog(shouldRequest);

                        }
                    }).callback(new PermissionUtils.FullCallback() {
                @Override
                public void onGranted(List<String> permissionsGranted) {

                    Log.i(TAG,"onGranted");
                    toCopyAssetsFile();
                }

                @Override
                public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                    Log.i(TAG,"onDenied");
                    if (!permissionsDeniedForever.isEmpty()) {
                        DialogHelper.showOpenAppSettingDialog();
                    }
                }
            }).request();


        } else {
            toCopyAssetsFile();
        }
    }

    private void toCopyAssetsFile() {
        // 程序启动本地日志记录
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native", "Native", "SmartADH5程序初始化!"));

        String wwwPath = SPManager.getManager().getString(SPManager.KEY_APP_WWW_FOLDER);

        // 是否执行更新
        if (HotCodeUpdateModule.init() || !(!TextUtils.isEmpty(wwwPath) && FileUtils.isFileExists(wwwPath))) {
            Logger.i("应用执行更新");
            HotCodeUpdateModule.doUpgrade();
        } else {
            delayToIndex();
        }
    }


    private void showProgress(long progress) {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        if (mProgressTip.getVisibility() == View.INVISIBLE) {
            mProgressTip.setVisibility(View.VISIBLE);
        }
        if (mLoading.getVisibility() == View.VISIBLE) {
            mLoading.setVisibility(View.GONE);
        }
        mProgressBar.setProgress((int) progress);
    }

    private void hideProgress() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
        if (mProgressTip.getVisibility() == View.VISIBLE) {
            mProgressTip.setVisibility(View.INVISIBLE);
        }
        if (mLoading.getVisibility() == View.GONE) {
            mLoading.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upgradeProgress(HotOutMsg msg) {

        // HotCode执行更新过程
        if (msg.isStart()) {
            // 移动开始或者正在进行
            showProgress(msg.getProgress());
            if (msg.getProgressTip() != null) {
                mProgressTip.setText(msg.getProgressTip());
            }
        }

        if (msg.isFinish()) {
            // 移动完成
            hideProgress();
            delayToIndex();
        }
    }

    /**
     * 设备未升级 && 设备升级完成
     * 延迟 2s 跳转向 .activity.IndexActivity
     */
    private void delayToIndex() {
        SmartToast.success("应用初始化完成, 即将进入应用, 请稍候!!");
        Config.getMainHandler().postDelayed(() -> {
            SPManager.getInstance().saveIsInitSuccess(true);
            // Jump to web
//            IndexActivity.actionStart(this);
            finish();
        }, 2000);
    }

}
