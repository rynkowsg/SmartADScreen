package cn.com.smartadscreen.locallog;

import android.content.Context;
import android.content.Intent;

import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.locallog.service.LocalLogIntentService;
import cn.com.smartadscreen.model.bean.config.Config;


/**
 * Created by Taro on 2017/3/13.
 * 本地日志记录工具类
 */
public class SmartLocalLog {

    public static final String EXTRA_LOG_MSG = "extra_log_msg";

    public static void writeLog(LogMsg logMsg){

        Context context = Config.getContext();
        Intent intent = new Intent(context, LocalLogIntentService.class);
        intent.putExtra(EXTRA_LOG_MSG, logMsg);
        context.startService(intent);

    }

}
