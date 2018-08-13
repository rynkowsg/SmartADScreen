package cn.com.smartadscreen.presenter.crash;

import android.content.Context;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.utils.DeviceInfoUtils;
import cn.startai.apkcommunicate.CommunicateType;
import cn.startai.apkcommunicate.StartaiCommunicate;

/**
 * 自定义异常处理器
 *
 * @author lobs
 */
public class UnCeHandler implements UncaughtExceptionHandler {

    private UncaughtExceptionHandler mDefaultHandler;
    public final String TAG = "CatchExcep";
    private Context context;

    public UnCeHandler(Context context) {
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            if (!handleException(ex) && mDefaultHandler != null) {
                // 如果用户没有处理则让系统默认的异常处理器来处理
                mDefaultHandler.uncaughtException(thread, ex);
            } else {

            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        try {
            if (ex == null) {
                return false;
            }
            ex.printStackTrace();
            // record
            ArrayList<String> remarks = new ArrayList<>();
            remarks.add(ex.getLocalizedMessage());
            StackTraceElement[] stackTrace = ex.getStackTrace();
            for (StackTraceElement element : stackTrace) {
                remarks.add(element.toString());
            }
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_EXCEPTION, "Native", "Native", "程序异常捕捉", remarks));

            String json = "{\"packageName\","+context.getPackageName()+"}";
            StartaiCommunicate.getInstance().send(context,CommunicateType.COMMUNICATE_TYPE_APP_CRASH,json);


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            long ramInfo=  DeviceInfoUtils.getInstance().getAvailMemory(context);
            String ss= String.valueOf(ramInfo);
            ArrayList<String> remarks = new ArrayList<>();
            remarks.add(ss);
            SmartLocalLog.writeLog(new LogMsg(LogMsg.TYPE_EXCEPTION, "Native", "Native", "剩余内存大小", remarks));
            String packageName = context.getPackageName();
            String json = "{\"packageName\":\""+ packageName+"\"}";
            StartaiCommunicate.getInstance().send(context, CommunicateType.COMMUNICATE_TYPE_APP_CRASH,json);
        }

        return true;
    }

}
