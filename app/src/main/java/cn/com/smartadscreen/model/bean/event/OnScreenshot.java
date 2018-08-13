package cn.com.startai.smartadh5.processlogic.entity.event;

/**
 * Created by Taro on 2017/5/23.
 * 截屏实体类
 */
public class OnScreenshot {
    private String fromid;
    private String screenShotPath;
    private String msgcw;

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public String getScreenShotPath() {
        return screenShotPath;
    }

    public void setScreenShotPath(String screenShotPath) {
        this.screenShotPath = screenShotPath;
    }

    public String getMsgcw() {
        return msgcw;
    }

    public void setMsgcw(String msgcw) {
        this.msgcw = msgcw;
    }
}
