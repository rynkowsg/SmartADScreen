package cn.com.startai.smartadh5.processlogic.entity.event;

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
