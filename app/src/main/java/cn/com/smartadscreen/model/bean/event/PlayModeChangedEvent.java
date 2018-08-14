package cn.com.smartadscreen.model.bean.event;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2018/2/28
 *         作用：播放模式改变状态
 */

public class PlayModeChangedEvent {

    //当前播放模式
    private int mode;

    public PlayModeChangedEvent(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
