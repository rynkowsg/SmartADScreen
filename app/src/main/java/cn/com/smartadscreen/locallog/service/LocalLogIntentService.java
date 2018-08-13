package cn.com.smartadscreen.locallog.service;

import android.app.IntentService;
import android.content.Intent;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.model.sp.SPManager;


public class LocalLogIntentService extends IntentService {

    private SimpleDateFormat allFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("MMdd", Locale.getDefault());

    public static final String PERFIX = "=============================================\n";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LocalLogIntentService(String name) {
        super(name);
    }

    public LocalLogIntentService() {
        this("LocalLogIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // 每次处理日志写入的 intent
        if (intent != null) {

            LogMsg logMsg = (LogMsg) intent.getSerializableExtra(SmartLocalLog.EXTRA_LOG_MSG);
            StringBuilder builder = new StringBuilder();

            builder.append(PERFIX);
            builder.append("创建时间: ");
            builder.append(TimeUtils.date2String(logMsg.getCreateDate(),allFormat));
            builder.append("\n");

            builder.append("日志类型: ");
            builder.append(logMsg.getType());
            builder.append("\n");

            builder.append("日志方向: ");
            builder.append(logMsg.getFrom());
            builder.append(" => ");
            builder.append(logMsg.getTo());
            builder.append("\n");

            builder.append("日志详细: ");
            builder.append(logMsg.getContent());
            builder.append("\n");

            builder.append("日志备注:\n\n");
            for (String remark : logMsg.getRemarks()) {
                builder.append(remark);
                builder.append("\n");
            }
            builder.append(PERFIX);
            builder.append("\n\n");

            String writeToFileString = builder.toString();

            String filePath = SPManager.getInstance().getSdcardPath() + "/startai/log/Log_smart_ad_h5_"
                    + TimeUtils.date2String(new Date(), timeFormat) + ".txt";
            // write
            FileIOUtils.writeFileFromString(filePath,writeToFileString,true);
        }


    }

}
