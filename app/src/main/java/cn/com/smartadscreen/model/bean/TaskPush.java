package cn.com.smartadscreen.model.bean;

import java.util.Date;

/**
 * Created by Taro on 2017/3/22.
 * 推送任务
 */
public class TaskPush {
    // 时间类型
    private String timeType;
    //logo图片
    private String logoPath;
    // Screen 的 ID
    private String screenId;
    // 该 Screen 对应的播表 ID
    private long tableId;
    // 推送的消息
    private String message;
    // 是否延时推送
    private boolean delay;
    // 延迟的时间信息
    private Date date;
    // 是否为新加
    private boolean isAdd;

    public TaskPush(String message) {
        this.message = message;
    }

    public TaskPush(String message, boolean delay, Date date) {
        this.message = message;
        this.delay = delay;
        this.date = date;
    }

    public TaskPush(String screenId, String message, boolean delay, Date date) {
        this.screenId = screenId;
        this.message = message;
        this.delay = delay;
        this.date = date;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDelay() {
        return delay;
    }

    public void setDelay(boolean delay) {
        this.delay = delay;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setIsAdd(boolean add) {
        isAdd = add;
    }
}
