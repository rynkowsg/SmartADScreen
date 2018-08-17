package cn.com.smartadscreen.presenter.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;


/**
 * Created by chufeng on 2018/5/17.
 */

public class AnrReceiver extends BroadcastReceiver {
    private static final String ACTION_ANR = "android.intent.action.ANR";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.v(" ANR Receiver onReceive");
        SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_EXCEPTION, "HTML", "ADH5", "ANR Receiver onReceive"));
        if (intent.getAction().equals(ACTION_ANR)) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    Context applicationContext = context.getApplicationContext();
                    Intent restartIntent = applicationContext.getPackageManager()
                            .getLaunchIntentForPackage(applicationContext.getPackageName());

                    PendingIntent pi = PendingIntent.getActivity(applicationContext, 0, restartIntent, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager alm = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
                    alm.set(AlarmManager.RTC, System.currentTimeMillis() + 50, pi);
                }
            }.start();
        }
    }
}
