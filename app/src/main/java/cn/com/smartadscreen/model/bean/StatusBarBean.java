package cn.com.smartadscreen.model.bean;

import android.text.TextUtils;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/7/24
 * 作用：H5 请求操作 StatusBar
 */

public class StatusBarBean {

    private String timeType;    //时间类型
    private String logoPath;    //logo路径

    public StatusBarBean(String timeType, String logoPath) {
        this.timeType = timeType;
        this.logoPath = logoPath;
    }

    public boolean isTimeShow() {
        return !TextUtils.isEmpty(timeType);
    }

    public boolean isLogoShow() {
        return !TextUtils.isEmpty(logoPath);
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

    @Override
    public boolean equals(Object obj) {
        return this == obj
                || (obj instanceof StatusBarBean
                    && TextUtils.equals(((StatusBarBean) obj).getTimeType(), this.timeType)
                    && TextUtils.equals(((StatusBarBean) obj).getLogoPath(), this.logoPath));
    }

    @Override
    public String toString() {
        return "StatusBarBean " +
                "timeType:" + timeType + ";  " +
                "logoPath:" + logoPath;
    }
}
