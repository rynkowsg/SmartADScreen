package cn.com.smartadscreen.presenter.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.utils.SmartToast;


/**
 * Created by Taro on 2017/3/19.
 * 接收指令
 */
public class CommandReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (Config.ACTION_ENABLED_HARDWARE_KEY.equals(action)) {
            boolean flag = intent.getBooleanExtra("flag", false);
            SPManager.getManager().put(SPManager.KEY_ENABLED_HARDWARE_KEY, flag);
        }

        if (Config.ACTION_ENABLED_TOAST_DOWNLOAD_INFO.equals(action)) {
            boolean flag = intent.getBooleanExtra("flag", false);
            SPManager.getManager().put(SPManager.KEY_ENABLED_TOAST_DOWNLOAD_INFO, flag);
        }

        if (Config.ACTION_SHOW_DOWNLOAD_PROGRESS.equals(action)) {

        }

        if (Config.ACTION_APP_USE_CROSSWALK.equals(action)) {
            boolean flag = intent.getBooleanExtra("flag", false);
            SmartToast.info("是否启用 CrossWalk 改变:" + flag);
            SPManager.getManager().put(SPManager.KEY_APP_USE_CROSSWALK, flag);
        }

    }

}
