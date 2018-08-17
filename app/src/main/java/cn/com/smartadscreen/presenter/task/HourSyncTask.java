package cn.com.smartadscreen.presenter.task;

import java.util.ArrayList;

import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.main.ui.activity.IndexActivity;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.db.manager.ScreenHelper;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;

/**
 * 每小时同步任务
 */
public class HourSyncTask implements Runnable{
    private final IndexActivity view;
    private int timeInterval;//时间间隔

    public HourSyncTask(int timeInterval, IndexActivity view) {
        this.timeInterval = timeInterval;
        this.view = view;
    }
    @Override
    public void run() {
        view.runOnUiThread(() -> {
            if (!Config.screenOff && !ScreenHelper.getInstance().hasBroadWillPlay()) {
                ArrayList<String> remarks = new ArrayList<>();
                remarks.add("播表同步间隔时间：" + timeInterval + "小时");
                SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_HANDLER, "Native",
                        "Native", "播表同步开始", remarks));
                DataSourceUpdateModule.playBtById("-1");
            }
        });

    }
}
