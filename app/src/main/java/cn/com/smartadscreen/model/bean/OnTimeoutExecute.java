package cn.com.smartadscreen.model.bean;

/**
 * Created by Taro on 2017/3/24.
 *
 */
public class OnTimeoutExecute {

    private String timeoutKey;

    public OnTimeoutExecute(String timeoutKey) {
        this.timeoutKey = timeoutKey;
    }

    public String getTimeoutKey() {
        return timeoutKey;
    }

    public void setTimeoutKey(String timeoutKey) {
        this.timeoutKey = timeoutKey;
    }
}
