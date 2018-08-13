package cn.com.smartadscreen.utils;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import it.sauronsoftware.cron4j.Scheduler;

/**
 * 名称：SmartTimerService
 * 描述：时钟服务，用于管理在指定时间段执行固定任务
 * <p/>
 * Created by Robin on 2016-6-17
 * QQ 419109715 彬影
 */
public class SmartTimerService {

    private static SmartTimerService instance;

    private static HashMap<String, SchedulerBean> mapScheduler = new HashMap<>();

    public static SmartTimerService getInstance() {
        if (instance == null) {
            instance = new SmartTimerService();
        }
        return instance;
    }


    private SmartTimerService() {
    }

    /**
     * 开启一个定时任务
     *
     * @param key            任务key
     * @param schedulingDate 开启任务的时间
     * @param task           需要执行的任务
     * @return {string} taskId
     */
    public String startTask(String key, Date schedulingDate, Runnable task) {
        // * * * * *
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedulingDate);
        return startTask(key, calendar, task);
    }


    public String startTask(String key, Calendar schedulingCalendar, Runnable task) {
        int second = schedulingCalendar.get(Calendar.SECOND);

        if(second > 0){
            schedulingCalendar.add(Calendar.MINUTE, 1);
        }
        int month = schedulingCalendar.get(Calendar.MONTH) + 1;
        int day = schedulingCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = schedulingCalendar.get(Calendar.HOUR_OF_DAY);
        int minutes = schedulingCalendar.get(Calendar.MINUTE);
        String pattern = minutes + " " + hour + " " + day + " " + month + " *";
        return startTask(key, pattern, task);
    }

    /**
     * 开启一个定时任务
     *
     * @param schedulingPattern 定点字符串
     * @param task              执行的任务
     * @return 任务id 可以用来重启可消除些任务
     */
    public String startTask(@NonNull String key, String schedulingPattern, Runnable task) {
        Logger.i("TimerTask任务启动:" + schedulingPattern + "   key==>" +key);
        Scheduler sche;
        String schedulerId = null;
        SchedulerBean sBean = mapScheduler.get(key);
        if (sBean == null) {
            sche = new Scheduler();
            schedulerId = sche.schedule(schedulingPattern, task);
            sche.start();
            sBean = new SchedulerBean(sche, schedulerId);
            mapScheduler.put(key, sBean);
        } else {
            sche = sBean.getScheduler();
            schedulerId = sBean.getSchedulerId();
            sche.reschedule(schedulerId, schedulingPattern);
            mapScheduler.put(key, sBean);
        }
        return schedulerId;
    }

    /**
     * 停止一个定时任务
     */
    public void stopTask(String key) {
        SchedulerBean sBean = mapScheduler.get(key);
        if (sBean != null) {
            Scheduler sche = sBean.getScheduler();
            if(sche != null) {
                if(sche.isStarted()) {
                    sche.stop();
                }
                sche.deschedule(sBean.getSchedulerId());
            }
            mapScheduler.remove(key);
        }
    }

    /**
     * 停止所有的定时
     */
    public void stopAllTask() {
        Collection<SchedulerBean> values = mapScheduler.values();
        for (SchedulerBean sBean : values) {
            Scheduler sche = sBean.getScheduler();
            if(sche != null) {
                if(sche.isStarted()) {
                    sche.stop();
                }
                sche.deschedule(sBean.getSchedulerId());
            }
        }
        mapScheduler.clear();
    }

    @Override
    public String toString() {
        return mapScheduler.toString();
    }

    /**
     * 任务调度器实体类
     * Scheduler 任务调度器
     * schedulerId 调用 schedule() 方法后调度程序分配的ID
     */
    private class SchedulerBean{
        //任务的调度器
        private Scheduler scheduler;
        //由调度程序分配的任务自动生成的ID。此ID可以在以后重新安排和调度任务，并且还可以检索关于它的信息。
        private String schedulerId;

        public SchedulerBean() {}

        SchedulerBean(Scheduler scheduler, String schedulerId) {
            this.scheduler = scheduler;
            this.schedulerId = schedulerId;
        }

        Scheduler getScheduler() {
            return scheduler;
        }

        public void setScheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
        }

        String getSchedulerId() {
            return schedulerId;
        }

        void setSchedulerId(String schedulerId) {
            this.schedulerId = schedulerId;
        }

        @Override
        public String toString() {
            return "SchedulerBean{" +
                    "scheduler=" + scheduler.getSchedulingPattern(String.valueOf(schedulerId)).toString() +
                    ", schedulerId='" + schedulerId + '\'' +
                    '}';
        }
    }
}
